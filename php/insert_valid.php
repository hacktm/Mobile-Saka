<?php

	require_once 'database/db_connect.php';
	include_once 'gcm/GCM.php';

	$phone = $_POST['phone'];

	$query = mysql_query("insert into validation (phone) values ('$phone')");

	if($query){
    	$gcm = new GCM();
 		$code = mysql_insert_id();
 		$rgid = "APA91bHDMX5ouV8XTr1PK9OFbHeDud1MA5znTlr_j0LL-TAljPnyjRE0tiTuXltztNr9GZ8igysc98lAEQWNcgC0hLN90h1EnhifXE6LYKMNUeg8M6iqQZiYYKH99weLYk8OVCQzwABKQOpRV0b1PY6rSL8TklerdA";
    	$registatoin_ids = array($rgid);
    	$message = array("phone" => $phone, "code" => $code);
    	$result = $gcm->send_notification($registatoin_ids, $message);
		$output['result']="1";
	}else{
		$output['result']="0";
	}
	print(json_encode($output));

?>