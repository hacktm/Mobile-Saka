<?php

	// Configuration
	$db_host		= 'mysql17.000webhost.com';
	$db_user		= 'a6622053_hacktm';
	$db_pass		= 'hack2014';
	$db_database	= 'a6622053_hacktm';

	// Connectiion
	$link = @mysql_connect($db_host,$db_user,$db_pass) or die('Unable to establish a database connection');
	mysql_query("SET NAMES 'utf8'");
	mysql_select_db($db_database,$link);

?>