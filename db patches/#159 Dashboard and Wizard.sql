# Addition of last login timestamp to the USERACCOUNT table
ALTER TABLE `USERACCOUNT` ADD `LAST_LOGIN` TIMESTAMP NULL DEFAULT NULL AFTER `USER_LOCKED`;
ALTER TABLE `USERACCOUNT` ADD `FIRST_LOGIN` BIT(1) NOT NULL AFTER `LAST_LOGIN`;

# Addition of preloader render flag
ALTER TABLE `PROGRAM` ADD `RENDER_PRELOADER` BIT(1) NOT NULL AFTER `OBJECTID`;
UPDATE `PROGRAM` SET `RENDER_PRELOADER` = 1 WHERE IS_PUBLIC = 0 AND PROGRAM_NAME NOT IN ('dashboard');

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

# Set dashboard as default program for Segmail Users
UPDATE PROGRAM_ASSIGNMENT assign 
JOIN PROGRAM prog ON assign.SOURCE = prog.OBJECTID AND prog.PROGRAM_NAME = 'dashboard'
JOIN USERTYPE type ON assign.TARGET = type.OBJECTID AND type.USERTYPENAME = 'Segmail User'
SET DEFAULT_ASSIGNMENT = 1;


# To fill in KEY_NAME for SUBSCRIPTION_LIST_FIELD so that Latest Subscribers in dashboard can show some records
# This does not change the behaviour of Subscription, Lists, and Subscribers. 
UPDATE SUBSCRIPTION_LIST_FIELD SET KEY_NAME = CONCAT('listfield',LPAD(OWNER,10,'0'),LPAD(SNO,5,'0')) WHERE KEY_NAME IS NULL;


# Create USER_SETTING table
--
-- Table structure for table `USER_SETTING`
--
CREATE TABLE `USER_SETTING` (
  `START_DATE` date NOT NULL,
  `SNO` int(11) NOT NULL,
  `END_DATE` date NOT NULL,
  `DATA_TYPE` varchar(255) NOT NULL,
  `CHANGED_BY` varchar(255) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATE_CHANGED` date DEFAULT NULL,
  `DATE_CREATED` date DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `OWNER` bigint(20) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `VALUE` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for table `USER_SETTING`
--
ALTER TABLE `USER_SETTING`
  ADD PRIMARY KEY (`START_DATE`,`SNO`,`OWNER`,`END_DATE`,`DATA_TYPE`);
