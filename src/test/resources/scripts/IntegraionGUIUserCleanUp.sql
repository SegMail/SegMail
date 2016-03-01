    
DELETE segmail.USER, segmail.USERACCOUNT  FROM segmail.USERACCOUNT INNER JOIN segmail.USER
WHERE
    segmail.USERACCOUNT.USERNAME = 'userTest' AND segmail.USER.OBJECTID = segmail.USERACCOUNT.OWNER;
    
DELETE FROM segmail.USERTYPE 
WHERE
    usertypename = 'test';	
