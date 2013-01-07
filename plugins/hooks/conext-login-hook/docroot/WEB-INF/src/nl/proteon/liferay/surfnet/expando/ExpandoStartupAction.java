package nl.proteon.liferay.surfnet.expando;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.DuplicateColumnNameException;
import com.liferay.portlet.expando.NoSuchTableException;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

public class ExpandoStartupAction extends SimpleAction {

	public final static String ACCESS_TOKEN_NAME = "accessToken";

	@Override
	public void run(String[] ids) throws ActionException {

		try {
			ExpandoTable table = expandoTable(Long.parseLong(ids[0]));
			List<ExpandoColumn> columns = new ArrayList<ExpandoColumn>();

			// FIXME: This is a kludge since this is not themeable.
			// set the width of our custom fields to 150px.
			UnicodeProperties textProperties = new UnicodeProperties();
			textProperties.setProperty(ExpandoColumnConstants.PROPERTY_WIDTH,
					"150");

			// define columns to be add
			columns.add(addExpandoColumn(table, ACCESS_TOKEN_NAME,
					ExpandoColumnConstants.STRING, textProperties, null));

			for (ExpandoColumn column : columns) {
				ResourcePermissionLocalServiceUtil.setResourcePermissions(
						PortalUtil.getDefaultCompanyId(),
						ExpandoColumn.class.getName(),
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(column.getColumnId()),
						RoleLocalServiceUtil.getRole(
								PortalUtil.getDefaultCompanyId(),
								RoleConstants.USER).getRoleId(), new String[] {
								ActionKeys.VIEW, ActionKeys.UPDATE });
			}

		} catch (Exception e) {
			_log.error(e);
		}
	}

	private ExpandoColumn addExpandoColumn(ExpandoTable table,
			String columnName, int columnType, UnicodeProperties properties,
			Object defaultData) throws PortalException, SystemException {

		ExpandoColumn column;

		long tableId = table.getTableId();

		try {
			column = ExpandoColumnLocalServiceUtil.addColumn(tableId,
					columnName, columnType, defaultData);
			_log.info("Adding expando field " + columnName);
		} catch (DuplicateColumnNameException dcne) {
			column = ExpandoColumnLocalServiceUtil.getColumn(tableId,
					columnName);
			_log.info("Checking expando field " + columnName
					+ ", already exists");
		}

		if (properties != null) {
			column.setTypeSettingsProperties(properties);
			ExpandoColumnLocalServiceUtil.updateExpandoColumn(column);
		}

		return column;
	}

	public static ExpandoTable expandoTable(long companyId)
			throws PortalException, SystemException {

		ExpandoTable table;

		try {
			table = ExpandoTableLocalServiceUtil.getDefaultTable(companyId,
					User.class.getName());
		} catch (NoSuchTableException nste) {
			table = ExpandoTableLocalServiceUtil.addDefaultTable(companyId,
					User.class.getName());
		}
		return table;
	}

	private static Log _log = LogFactoryUtil.getLog(ExpandoStartupAction.class);
}
