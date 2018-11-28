<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1" shrink-to-fit=no">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" type="text/css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/theme.css" type="text/css">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" type="text/css">
</head>
<body>
  <nav class="navbar navbar-dark bg-dark">
    <div class="container d-flex justify-content-center"> <a class="navbar-brand" href="#">
        <b> Library Management System</b>
      </a> </div>
  </nav>
  <div class="py-3">
    <div class="container">
      <div class="row">
      <div class="col-md-12"><a class="btn btn-link" href="loginPage.jsp" style="float: right;">Log Out</a></div>
        <div class="col-md-12 text-center d-md-flex justify-content-between align-items-center">
          <ul class="nav d-flex justify-content-center">
            <li class="nav-item"> <form action="ControllerServlet" method="post">
            							<input type="hidden" id="action" value="calling_browse_books"/>
                                        <input type="submit" value="Browse Books"/> </form></li>
            <li class="nav-item"> <form action="ControllerServlet" method="post">
            							<input type="hidden" id="action" value="calling_view_your_books"/>
                                        <input type="submit" value="View Your Books"/> </form> </li>
            <li class="nav-item"> <form action="ControllerServlet" method="post">
            							<input type="hidden" id="action" value="calling_edit_your_details"/>
                                        <input type="submit" value="Edit Your Details"/> </form></li>
            </ul>
        </div>
      </div>
    </div>
  </div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>

</body>

</html>