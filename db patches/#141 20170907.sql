# Update all SUBSCRIBER_ACCOUNT.SUBSCRIBER_STATUS from ACTIVE to VERIFIED
update SUBSCRIBER_ACCOUNT set SUBSCRIBER_STATUS = 'VERIFIED' where SUBSCRIBER_STATUS = 'ACTIVE';

# This is for the new dashboard changes
# Update all Menu grouping for MENUITEM_ACCESS before deploying code changes and config changes
ALTER TABLE `MENUITEM_ACCESS` ADD `MENU_GROUP` VARCHAR(255) NULL DEFAULT NULL AFTER `MENU_ORDER`;
update MENUITEM_ACCESS set MENU_GROUP = 'LEFT';
update MENUITEM_ACCESS set MENU_GROUP = 'PROFILE' where SOURCE in (select OBJECTID from MENUITEM where MENU_ITEM_URL = '/program/mysettings/');
# Set Dashboard as the default program for Segmail Users
update PROGRAM_ASSIGNMENT 
set DEFAULT_ASSIGNMENT = false
WHERE
	TARGET in (select OBJECTID from USERTYPE where USERTYPENAME in ('Segmail User'));

update PROGRAM_ASSIGNMENT 
set DEFAULT_ASSIGNMENT = true
WHERE
	(TARGET,SOURCE,REL_SEQUENCE) in (
        select ass.TARGET, ass.SOURCE, ass.REL_SEQUENCE
        from (select * from PROGRAM_ASSIGNMENT) ass
        left join PROGRAM p on ass.SOURCE = p.OBJECTID
        left join USERTYPE t on ass.TARGET = t.OBJECTID
        where 
        	p.PROGRAM_NAME = 'dashboard'
        	and t.USERTYPENAME in ('Segmail User')
    	);
