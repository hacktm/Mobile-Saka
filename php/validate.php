<?php

	require_once 'database/db_connect.php';

	$code = $_POST['code'];
	$phone = $_POST['phone'];

	$query = mysql_query("select * from validation where code='$code' and phone = '$phone'");

	if($row = mysql_fetch_assoc($query)){
		$output['result'] = '1';
		mysql_query("delete from validation where phone = '$phone'");
	}else{
		$output['result'] = '0';
	}

	print(json_encode($output));

?>