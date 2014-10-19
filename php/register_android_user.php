<?php

	require_once 'database/db_connect.php';
	include_once 'gcm/GCM.php';

	$phone = $_POST['phone'];
	$regid = $_POST['gmc_id'];
	$driver = $_POST['driver'];

	if($driver=='1'){
		$q = mysql_query("insert into drivers_android (phone, gcm_id) values ('$phone', '$regid')");
	}else{
		$q = mysql_query("insert into clients_android (phone, gcm_id) values ('$phone', '$regid')");
	}

	if($q){
		$output['result']="1";
	}else{
		$output['result']="0";
	}
	print(json_encode($output));

?>