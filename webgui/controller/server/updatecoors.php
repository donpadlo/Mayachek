<?php
$userid=GetDef("userid");
$Longitude=GetDef("Longitude");
$Latitude=GetDef("Latitude");
$type=GetDef("type");
$dt=GetDef("dt");
$dt=str_replace("v"," ",$dt);
if ($type=="GPS"):
     $type=1;
    else:
     $type=0;
endif;
$sql="insert into coords (id,userid,dt,Longitude,Latitude,type) values (null,:userid,:dt,:Longitude,:Latitude,:type)";
try {
    $pre=$cfg->db->prepare($sql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
    $pre->execute(array(':userid' => $userid,':dt' => $dt, ':Longitude' => $Longitude, ':Latitude' => $Latitude, ':type' => $type));
    echo "Ok $dt";
} catch (PDOException $e) {
    print "ERROR!: " . $e->getMessage() . "<br/>";
    die();
}
?>