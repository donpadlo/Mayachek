<div class="container-fluid"> 
<div class="row">
  <div class="col-xs-4 col-md-4 col-sx-4"></div>
  <div class="col-xs-4 col-md-4 col-sx-4">
    
	<hr/>
	<input class="form-control" type="userid" class="form-control" id="userid" placeholder="Введите ID маячка">
	<button class="form-control" onclick="goOnline()" class="btn btn-default">Последнее местоположение</button>
	<button class="form-control" onclick="goTrack()" class="btn btn-default">История передвижений</button>
    
  </div>
  <div class="col-xs-4 col-md-4 col-sx-4"></div>
</div>    
</div>
<script>
function goOnline(){
	userid = document.getElementById('userid').value;
	document.location.href = "http://маячек.грибовы.рф/index.php?action=viewmeonline&userid="+userid;
};    
function goTrack(){
	userid = document.getElementById('userid').value;
	document.location.href = "http://маячек.грибовы.рф/index.php?action=viewtrack&userid="+userid;
};    
</script>    