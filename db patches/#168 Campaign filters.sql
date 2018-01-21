-- --------------------------------------------------------

--
-- Table structure for table `ASSIGN_CAMPAIGNACTIVITY_LIST`
--

CREATE TABLE `ASSIGN_CAMPAIGNACTIVITY_LIST` (
  `REL_SEQUENCE` int(11) NOT NULL,
  `CHANGED_BY` varchar(255) DEFAULT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `DATE_CHANGED` date DEFAULT NULL,
  `DATE_CREATED` date DEFAULT NULL,
  `SOURCE_TYPE` varchar(255) DEFAULT NULL,
  `TARGET_TYPE` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `TARGET` bigint(20) NOT NULL,
  `SOURCE` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ASSIGN_CAMPAIGNACTIVITY_LIST`
--
ALTER TABLE `ASSIGN_CAMPAIGNACTIVITY_LIST`
  ADD PRIMARY KEY (`TARGET`,`SOURCE`,`REL_SEQUENCE`);


-- --------------------------------------------------------

--
-- Table structure for table `CAMPAIGN_ACTIVITY_FILTER`
--

CREATE TABLE `CAMPAIGN_ACTIVITY_FILTER` (
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
  `FIELD_DISPLAY` varchar(255) DEFAULT NULL,
  `FIELD_KEY` varchar(255) DEFAULT NULL,
  `OPERATOR` varchar(255) DEFAULT NULL,
  `VALUE` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `CAMPAIGN_ACTIVITY_FILTER`
--
ALTER TABLE `CAMPAIGN_ACTIVITY_FILTER`
  ADD PRIMARY KEY (`START_DATE`,`SNO`,`OWNER`,`END_DATE`,`DATA_TYPE`);


--
-- Indexes for table `SUBSCRIBER_FIELD_VALUE`
--
ALTER TABLE `SUBSCRIBER_FIELD_VALUE`
  ADD KEY `ValueIndex` (`OWNER`,`FIELD_KEY`,`VALUE`);
