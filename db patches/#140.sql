-- Solving the datasync overwriting issue
update SUBSCRIPTION set STATUS = 'BOUNCED' where SOURCE in (select objectid from SUBSCRIBER_ACCOUNT where SUBSCRIBER_STATUS = 'BOUNCED');