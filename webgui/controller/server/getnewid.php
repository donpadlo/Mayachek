<?php
try {
    $sql="insert into users (id) values (null)";
    $cfg->db->query($sql);
    $userid=$cfg->db->lastInsertId();
    echo $userid;
} catch (PDOException $e) {
    print "ERROR!: " . $e->getMessage() . "<br/>";
    die();
}
