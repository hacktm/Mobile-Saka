<?php

	require_once 'database/db_connect.php';

	$query = mysql_query("select * from requests");

	$output['result']='0';
	$output['data']=array();
	while($row = mysql_fetch_assoc($query)){
		$output['result'] = '1';
		$output['data'][]=$row;
		
	}
	print(json_encode($output));

?>