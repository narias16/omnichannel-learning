var express = require("express"),
              app = express(),
              http = require("http").Server(app).listen(80);
console.log("Started at port 80")

app.use(express.static(__dirname + '/public')); 

app.get("/",function(req,res){
  res.sendFile(__dirname+"/public/html/courses.html");
})

app.get("/course-details",function(req,res) {
  res.sendFile(__dirname+"/public/html/course-details.html");
})

app.get("/geodata/", function(req, res) {
  res.sendFile(__dirname + "/public/html/geodata.html");
})