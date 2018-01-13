-- 1. Add unique keys to all EnterpriseTransaction tables on the column TRANSACTION_KEY
-- Example: ALTER TABLE `TRIGGER_EMAIL_ACTIVITY` ADD UNIQUE KEY `UK_pvt6j88fp8lukwfx01sxkhgo1` (`TRANSACTION_KEY`)
-- SELECT DISTINCT
--    TABLE_NAME
-- FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_SCHEMA = 'segmail' and COLUMN_NAME = 'TRANSACTION_KEY';

ALTER TABLE `BATCH_JOB_RUN_ERROR` DROP INDEX `UK_sc1xei37x9q9ldapn7lo559ie`;
ALTER TABLE `BATCH_JOB_RUN_ERROR` ADD UNIQUE KEY `UK_sc1xei37x9q9ldapn7lo559ie` (`TRANSACTION_KEY`);

ALTER TABLE `CAMPAIGN_LINK_CLICK` DROP INDEX `UK_6faefr3qjn515474trxmf6u1x`;
ALTER TABLE `CAMPAIGN_LINK_CLICK` ADD UNIQUE KEY `UK_6faefr3qjn515474trxmf6u1x` (`TRANSACTION_KEY`);

ALTER TABLE `FILE_TRANSACTION` DROP INDEX `UK_op7wy5phydx05mjcunkgquk4f`;
ALTER TABLE `FILE_TRANSACTION` ADD UNIQUE KEY `UK_op7wy5phydx05mjcunkgquk4f` (`TRANSACTION_KEY`);

ALTER TABLE `MAILMERGE_REQUEST` DROP INDEX `UK_5fqdg4ic042oshup0bkc6wy9x`;
ALTER TABLE `MAILMERGE_REQUEST` ADD UNIQUE KEY `UK_5fqdg4ic042oshup0bkc6wy9x` (`TRANSACTION_KEY`);

ALTER TABLE `PASSWORD_RESET_REQUEST` DROP INDEX `UK_q7a63y0pj9soubuyyw0x7oeva`;
ALTER TABLE `PASSWORD_RESET_REQUEST` ADD UNIQUE KEY `UK_q7a63y0pj9soubuyyw0x7oeva` (`TRANSACTION_KEY`);

ALTER TABLE `TRIGGER_EMAIL_ACTIVITY` DROP INDEX `UK_pvt6j88fp8lukwfx01sxkhgo1`;
ALTER TABLE `TRIGGER_EMAIL_ACTIVITY` ADD UNIQUE KEY `UK_pvt6j88fp8lukwfx01sxkhgo1` (`TRANSACTION_KEY`);

ALTER TABLE `TRIGGER_PASSWORD_USER`  DROP INDEX `UK_701syhpacxt53vsr3jfo56d5q`;
ALTER TABLE `TRIGGER_PASSWORD_USER`  ADD UNIQUE KEY `UK_701syhpacxt53vsr3jfo56d5q` (`TRANSACTION_KEY`);


-- 2. For all EnterpriseTransactionTrigger, change the column TRIGGERED_TRANSACTION from bigint(20) to varchar(255)
-- SELECT DISTINCT TABLE_NAME
-- FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_SCHEMA = 'segmail' and COLUMN_NAME = 'TRIGGERED_TRANSACTION';

SET sql_mode = 'STRICT_ALL_TABLES';
ALTER TABLE TRIGGER_EMAIL_ACTIVITY CHANGE TRIGGERED_TRANSACTION TRIGGERED_TRANSACTION VARCHAR(255);
ALTER TABLE TRIGGER_PASSWORD_USER CHANGE TRIGGERED_TRANSACTION TRIGGERED_TRANSACTION VARCHAR(255);

-- Migrate all TRIGGERED_TRANSACTION column values to TRANSACTION_KEY of the Triggered TX
UPDATE `TRIGGER_EMAIL_ACTIVITY` trigg join `EMAIL` email on trigg.`TRIGGERED_TRANSACTION` = email.`TRANSACTION_ID` 
SET trigg.`TRIGGERED_TRANSACTION` = email.`TRANSACTION_KEY`;
UPDATE `TRIGGER_PASSWORD_USER` trigg join `PASSWORD_RESET_REQUEST` req on trigg.`TRIGGERED_TRANSACTION` = req.`TRANSACTION_ID` 
SET trigg.`TRIGGERED_TRANSACTION` = req.`TRANSACTION_KEY`;

-- 3. Create all EMAIL tables
-- --------------------------------------------------------

--
-- Table structure for table `EMAIL_BOUNCED`
--

CREATE TABLE IF NOT EXISTS `EMAIL_BOUNCED` (
  `TRANSACTION_ID` bigint(20) NOT NULL,
  `CHANGED_BY` varchar(255) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATETIME_CHANGED` datetime DEFAULT NULL,
  `DATETIME_CREATED` datetime DEFAULT NULL,
  `EXPIRY_DATETIME` datetime DEFAULT NULL,
  `PROCESSING_STATUS` varchar(255) DEFAULT NULL,
  `PROGRAM` varchar(255) DEFAULT NULL,
  `SCHEDULED_DATETIME` datetime DEFAULT NULL,
  `TRANSACTION_KEY` varchar(255) DEFAULT NULL,
  `AWS_SES_MESSAGE_ID` varchar(255) DEFAULT NULL,
  `BODY` mediumtext,
  `RETRIES` int(11) NOT NULL,
  `SENDER_ADDRESS` varchar(255) DEFAULT NULL,
  `SENDER_NAME` varchar(255) DEFAULT NULL,
  `SUBJECT` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `EMAIL_BOUNCED`
--
ALTER TABLE `EMAIL_BOUNCED`
  ADD PRIMARY KEY (`TRANSACTION_ID`),
  ADD UNIQUE KEY `UK_h6rbk578kathynme02qfmr8fl` (`TRANSACTION_KEY`),
  ADD KEY `EMAIL_BOUNCEDEMAILTxKey` (`TRANSACTION_KEY`),
  ADD KEY `EMAIL_BOUNCEDMailServiceOutbound` (`TRANSACTION_ID`,`PROCESSING_STATUS`,`SCHEDULED_DATETIME`,`DATETIME_CHANGED`,`AWS_SES_MESSAGE_ID`);

--
-- Table structure for table `EMAIL_QUEUED`
--

CREATE TABLE IF NOT EXISTS `EMAIL_QUEUED` (
  `TRANSACTION_ID` bigint(20) NOT NULL,
  `CHANGED_BY` varchar(255) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATETIME_CHANGED` datetime DEFAULT NULL,
  `DATETIME_CREATED` datetime DEFAULT NULL,
  `EXPIRY_DATETIME` datetime DEFAULT NULL,
  `PROCESSING_STATUS` varchar(255) DEFAULT NULL,
  `PROGRAM` varchar(255) DEFAULT NULL,
  `SCHEDULED_DATETIME` datetime DEFAULT NULL,
  `TRANSACTION_KEY` varchar(255) DEFAULT NULL,
  `AWS_SES_MESSAGE_ID` varchar(255) DEFAULT NULL,
  `BODY` mediumtext,
  `RETRIES` int(11) NOT NULL,
  `SENDER_ADDRESS` varchar(255) DEFAULT NULL,
  `SENDER_NAME` varchar(255) DEFAULT NULL,
  `SUBJECT` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `EMAIL_QUEUED`
--
ALTER TABLE `EMAIL_QUEUED`
  ADD PRIMARY KEY (`TRANSACTION_ID`),
  ADD UNIQUE KEY `UK_oiaamqw7qej3w4c86o0y5tyst` (`TRANSACTION_KEY`),
  ADD KEY `EMAIL_QUEUEDEMAILTxKey` (`TRANSACTION_ID`,`TRANSACTION_KEY`),
  ADD KEY `EMAIL_QUEUEDMailServiceOutbound` (`TRANSACTION_ID`,`PROCESSING_STATUS`,`SCHEDULED_DATETIME`,`DATETIME_CHANGED`,`AWS_SES_MESSAGE_ID`);


--
-- Table structure for table `EMAIL_SENT`
--

CREATE TABLE IF NOT EXISTS `EMAIL_SENT` (
  `TRANSACTION_ID` bigint(20) NOT NULL,
  `CHANGED_BY` varchar(255) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATETIME_CHANGED` datetime DEFAULT NULL,
  `DATETIME_CREATED` datetime DEFAULT NULL,
  `EXPIRY_DATETIME` datetime DEFAULT NULL,
  `PROCESSING_STATUS` varchar(255) DEFAULT NULL,
  `PROGRAM` varchar(255) DEFAULT NULL,
  `SCHEDULED_DATETIME` datetime DEFAULT NULL,
  `TRANSACTION_KEY` varchar(255) DEFAULT NULL,
  `AWS_SES_MESSAGE_ID` varchar(255) DEFAULT NULL,
  `BODY` mediumtext,
  `RETRIES` int(11) NOT NULL,
  `SENDER_ADDRESS` varchar(255) DEFAULT NULL,
  `SENDER_NAME` varchar(255) DEFAULT NULL,
  `SUBJECT` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `EMAIL_SENT`
--
ALTER TABLE `EMAIL_SENT`
  ADD PRIMARY KEY (`TRANSACTION_ID`),
  ADD UNIQUE KEY `UK_mcsy3civa9lay3m0ugwmdp9ws` (`TRANSACTION_KEY`),
  ADD KEY `EMAIL_SENTEMAILTxKey` (`TRANSACTION_ID`,`TRANSACTION_KEY`),
  ADD KEY `EMAIL_SENTMailServiceOutbound` (`TRANSACTION_ID`,`PROCESSING_STATUS`,`SCHEDULED_DATETIME`,`DATETIME_CHANGED`,`AWS_SES_MESSAGE_ID`),
  ADD KEY `Tx_key` (`TRANSACTION_KEY`);


--
-- Table structure for table `EMAIL_ERROR`
--

CREATE TABLE IF NOT EXISTS `EMAIL_ERROR` (
  `TRANSACTION_ID` bigint(20) NOT NULL,
  `CHANGED_BY` varchar(255) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATETIME_CHANGED` datetime DEFAULT NULL,
  `DATETIME_CREATED` datetime DEFAULT NULL,
  `EXPIRY_DATETIME` datetime DEFAULT NULL,
  `PROCESSING_STATUS` varchar(255) DEFAULT NULL,
  `PROGRAM` varchar(255) DEFAULT NULL,
  `SCHEDULED_DATETIME` datetime DEFAULT NULL,
  `TRANSACTION_KEY` varchar(255) DEFAULT NULL,
  `AWS_SES_MESSAGE_ID` varchar(255) DEFAULT NULL,
  `BODY` mediumtext,
  `RETRIES` int(11) NOT NULL,
  `SENDER_ADDRESS` varchar(255) DEFAULT NULL,
  `SENDER_NAME` varchar(255) DEFAULT NULL,
  `SUBJECT` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `EMAIL_ERROR`
--
ALTER TABLE `EMAIL_ERROR`
  ADD PRIMARY KEY (`TRANSACTION_ID`),
  ADD UNIQUE KEY `UK_ib6x4n5rab0h0ek9jdrp8pc9l` (`TRANSACTION_KEY`),
  ADD KEY `EMAIL_ERROREMAILTxKey` (`TRANSACTION_KEY`),
  ADD KEY `EMAIL_ERRORMailServiceOutbound` (`TRANSACTION_ID`,`PROCESSING_STATUS`,`SCHEDULED_DATETIME`,`DATETIME_CHANGED`,`AWS_SES_MESSAGE_ID`);


-- 4. New batch job tables

--
-- Table structure for table `BATCH_JOB_RUN_COMPLETED`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_RUN_COMPLETED` (
  `RUN_KEY` varchar(255) NOT NULL,
  `CANCEL_TIME` datetime DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATETIME_CREATED` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `QUEUED_TIME` datetime DEFAULT NULL,
  `RUN_BY` varchar(255) DEFAULT NULL,
  `SCHEDULED_TIME` datetime DEFAULT NULL,
  `SERVER_NAME` varchar(255) DEFAULT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(255) DEFAULT NULL,
  `BATCH_JOB` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `BATCH_JOB_RUN_COMPLETED`
--
ALTER TABLE `BATCH_JOB_RUN_COMPLETED`
  ADD PRIMARY KEY (`RUN_KEY`),
  ADD KEY `BATCH_JOB_RUN_COMPLETEDgetNextNJobRuns` (`SCHEDULED_TIME`);



--
-- Table structure for table `BATCH_JOB_RUN_IN_PROCESS`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_RUN_IN_PROCESS` (
  `RUN_KEY` varchar(255) NOT NULL,
  `CANCEL_TIME` datetime DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATETIME_CREATED` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `QUEUED_TIME` datetime DEFAULT NULL,
  `RUN_BY` varchar(255) DEFAULT NULL,
  `SCHEDULED_TIME` datetime DEFAULT NULL,
  `SERVER_NAME` varchar(255) DEFAULT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(255) DEFAULT NULL,
  `BATCH_JOB` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `BATCH_JOB_RUN_IN_PROCESS`
--
ALTER TABLE `BATCH_JOB_RUN_IN_PROCESS`
  ADD PRIMARY KEY (`RUN_KEY`),
  ADD KEY `BATCH_JOB_RUN_IN_PROCESSgetNextNJobRuns` (`SCHEDULED_TIME`);


--
-- Table structure for table `BATCH_JOB_RUN_QUEUED`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_RUN_QUEUED` (
  `RUN_KEY` varchar(255) NOT NULL,
  `CANCEL_TIME` datetime DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATETIME_CREATED` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `QUEUED_TIME` datetime DEFAULT NULL,
  `RUN_BY` varchar(255) DEFAULT NULL,
  `SCHEDULED_TIME` datetime DEFAULT NULL,
  `SERVER_NAME` varchar(255) DEFAULT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(255) DEFAULT NULL,
  `BATCH_JOB` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `BATCH_JOB_RUN_QUEUED`
--
ALTER TABLE `BATCH_JOB_RUN_QUEUED`
  ADD PRIMARY KEY (`RUN_KEY`),
  ADD KEY `BATCH_JOB_RUN_QUEUEDgetNextNJobRuns` (`SCHEDULED_TIME`);


--
-- Table structure for table `BATCH_JOB_RUN_SCHEDULED`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_RUN_SCHEDULED` (
  `RUN_KEY` varchar(255) NOT NULL,
  `CANCEL_TIME` datetime DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATETIME_CREATED` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `QUEUED_TIME` datetime DEFAULT NULL,
  `RUN_BY` varchar(255) DEFAULT NULL,
  `SCHEDULED_TIME` datetime DEFAULT NULL,
  `SERVER_NAME` varchar(255) DEFAULT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(255) DEFAULT NULL,
  `BATCH_JOB` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `BATCH_JOB_RUN_SCHEDULED`
--
ALTER TABLE `BATCH_JOB_RUN_SCHEDULED`
  ADD PRIMARY KEY (`RUN_KEY`),
  ADD KEY `BATCH_JOB_RUN_SCHEDULEDgetNextNJobRuns` (`SCHEDULED_TIME`);


--
-- Table structure for table `BATCH_JOB_RUN_CANCELLED`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_RUN_CANCELLED` (
  `RUN_KEY` varchar(255) NOT NULL,
  `CANCEL_TIME` datetime DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATETIME_CREATED` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `QUEUED_TIME` datetime DEFAULT NULL,
  `RUN_BY` varchar(255) DEFAULT NULL,
  `SCHEDULED_TIME` datetime DEFAULT NULL,
  `SERVER_NAME` varchar(255) DEFAULT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(255) DEFAULT NULL,
  `BATCH_JOB` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `batch_job_run_cancelled`
--
ALTER TABLE `BATCH_JOB_RUN_CANCELLED`
  ADD PRIMARY KEY (`RUN_KEY`),
  ADD KEY `BATCH_JOB_RUN_CANCELLEDgetNextNJobRuns` (`SCHEDULED_TIME`);


--
-- Table structure for table `BATCH_JOB_RUN_FAILED`
--

CREATE TABLE IF NOT EXISTS `BATCH_JOB_RUN_FAILED` (
  `RUN_KEY` varchar(255) NOT NULL,
  `CANCEL_TIME` datetime DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATETIME_CREATED` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `QUEUED_TIME` datetime DEFAULT NULL,
  `RUN_BY` varchar(255) DEFAULT NULL,
  `SCHEDULED_TIME` datetime DEFAULT NULL,
  `SERVER_NAME` varchar(255) DEFAULT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(255) DEFAULT NULL,
  `BATCH_JOB` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `BATCH_JOB_RUN_FAILED`
--
ALTER TABLE `BATCH_JOB_RUN_FAILED`
  ADD PRIMARY KEY (`RUN_KEY`),
  ADD KEY `BATCH_JOB_RUN_FAILEDgetNextNJobRuns` (`SCHEDULED_TIME`);


-- 5. Removing constraints on existing BatchJob tables

ALTER TABLE `BATCH_JOB_RUN_LOG` DROP FOREIGN KEY `FKk2vv826llowym2vumtdp7nnwn`;
ALTER TABLE BATCH_JOB_RUN_LOG CHANGE BATCH_JOB_RUN_RUN_KEY BATCH_JOB_RUN VARCHAR(255);

-- 6. Removing constraints on existing Email_RECIPIENTS and Email_REPLY_TO_ADDRESSES

ALTER TABLE Email_RECIPIENTS DROP FOREIGN KEY FKp3l4rmuexm3y6am0d50bin2wv;
ALTER TABLE Email_REPLY_TO_ADDRESSES DROP FOREIGN KEY FKt8kptkddw11495aw54eyd9h1c; 

-- 7. Migrate all EMAIL records to their respective status tables

INSERT INTO EMAIL_SENT SELECT * FROM EMAIL WHERE PROCESSING_STATUS = 'SENT';
INSERT INTO EMAIL_BOUNCED SELECT * FROM EMAIL WHERE PROCESSING_STATUS = 'BOUNCED';
INSERT INTO EMAIL_ERROR SELECT * FROM EMAIL WHERE PROCESSING_STATUS = 'ERROR';
INSERT INTO EMAIL_QUEUED SELECT * FROM EMAIL WHERE PROCESSING_STATUS = 'QUEUED';

-- 8. Create indices for AWS_SES_MESSAGE_ID on all Email tables

ALTER TABLE EMAIL_SENT ADD KEY EMAIL_SENTMailServiceInbound (AWS_SES_MESSAGE_ID);
ALTER TABLE EMAIL_BOUNCED ADD KEY EMAIL_BOUNCEDMailServiceInbound (AWS_SES_MESSAGE_ID);
ALTER TABLE EMAIL_ERROR ADD KEY EMAIL_ERRORMailServiceInbound (AWS_SES_MESSAGE_ID);
ALTER TABLE EMAIL_QUEUED ADD KEY EMAIL_QUEUEDMailServiceInbound (AWS_SES_MESSAGE_ID);
