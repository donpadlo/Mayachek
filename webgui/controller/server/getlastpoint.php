<?php
$userid=  GetDef("userid");
$type=  GetDef("type");
$coods["Longitude"]=0;
$coods["Latitude"]=0;
$coods["dt"]="non";

$sql="select * from coords where userid=:userid and type=:type order by id desc limit 1";
try {
	$pre=$cfg->db->prepare($sql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));	
	$res=$pre->execute(array(':userid' => $userid,':type' => $type));	
	while ($row = $pre->fetch(PDO::FETCH_LAZY)){
	    $coods["Longitude"]=$row["Longitude"];
	    $coods["Latitude"]=$row["Latitude"];
	    $coods["dt"]=$row["dt"];
	};
	echo json_encode($coods);
} catch (PDOException $e) {
    print "ERROR!: " . $e->getMessage() . "<br/>";
    die();
}