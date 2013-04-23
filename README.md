liferayConext
=============

A connection library for SAML2 AuthN and VOOT based groups for Liferay

This component is a sttandard Liferay Plugin. 
For requirements and Prerequisites, see: http://www.liferay.com/community/wiki/-/wiki/Main/Plugins+SDK

Build:
* git clone https://github.com/OpenConextApps/liferayConext
* cd plugins
* ant all

The following properties can be overridden by adding a portlet-ext.properties file to the classpath of the portal.

## SAML Properties
* saml2.header.mapping.id=persistent-id
* saml2.header.mapping.screenname=persistent-id
* saml2.header.mapping.email=emailaddress
* saml2.header.mapping.fullname=displayname

## VOOT (Oauth/REST) properties
* opensocial.server.url=https://api.surfconext.nl/v1/
* opensocial.consumer.key=
* opensocial.consumer.secret=
* opensocial.redirect.url=


Licence
Copyright 2012 SURFnet bv, The Netherlands

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
