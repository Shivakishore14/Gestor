package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"log"
	"net"
	"net/http"
)

var database = "gestor"
var user = "test"
var password = "test"
var clientPort = "9007"

type sysCmd struct {
	Ip  []string `json:"ip"`
	Cmd string   `json:"cmd"`
}

func dBData() bool {
	db, err := sql.Open("mysql", user+":"+password+"@/"+database)
	if err = db.Ping(); err != nil {
		log.Fatal(err)
		return false
	}
	var ip, name string
	defer db.Close()
	rows, errs := db.Query("select ip , name from clients")
	if errs != nil {
		log.Fatal(err)
	}
	defer rows.Close()
	for rows.Next() {
		err := rows.Scan(&ip, &name)
		if err != nil {
			log.Fatal(err)
		}
		log.Println(ip, name)
	}
	err = rows.Err()
	if err != nil {
		log.Fatal(err)
	}
	return true
}
func sendToJava(s string) (bool, string){
	s = s + "\n" 
	servAddr := "localhost:4444"
	tcpAddr, err := net.ResolveTCPAddr("tcp", servAddr)
	if err != nil {
		println("ResolveTCPAddr failed: ", err.Error())
		return false, ""
	}

	conn, err := net.DialTCP("tcp", nil, tcpAddr)
	defer conn.Close()	
	if err != nil {
		println("Dial failed: ", err.Error())
		return false, ""
	}

	_, err = conn.Write([]byte(s))
	if err != nil {
		println("Write to server failed: ", err.Error())
		return false, ""
	}

	reply := make([]byte, 1024)
	_, err = conn.Read(reply)

	if err != nil {
		println("Write to server failed: ", err.Error())
		return false, ""
	}

	println("reply from server= ", string(reply))
	return true, string(reply)
	
}
func sendToClient(ip string, cmd string) (bool, string){
	cmd = cmd + "\n"
	servAddr := ip+ ":" +clientPort
	reply := ""
	tcpAddr, err := net.ResolveTCPAddr("tcp", servAddr)
	if err != nil {
		println("ResolveTCPAddr failed: ", err.Error())
		return false, ""
	}

	conn, err := net.DialTCP("tcp", nil, tcpAddr)
	defer conn.Close()	
	if err != nil {
		println("Dial failed: ", err.Error())
		return false, ""
	}
	
	bCmd := []byte(cmd)
	_, err = conn.Write(bCmd)
	if err != nil {
		println("Write to server failed: ", err.Error())
		return false, ""
	}
	if (string(bCmd[0]) == "Y") {
		reply := make([]byte, 1024)
		_, err = conn.Read(reply)
	}
	if err != nil {
		println("Write to server failed: ", err.Error())
		return false, ""
	}

	println("reply from server= ", string(reply))
	return true, string(reply)
	
}
//http handlers
func getHandler(w http.ResponseWriter, r *http.Request) {
	s := "<li class=\"figures\" > <img src=\"images/pc.ico\" alt=\"%s\" onclick=\"javascript:imgClicked(this)\" hspace=\"30\" class=\"figure\"/><span class=\"figcaption\"> %s </span></li>"
	a := ""
	db, err := sql.Open("mysql", user+":"+password+"@/"+database)
	if err = db.Ping(); err != nil {
		log.Fatal(err)
	}
	var ip, name string
	defer db.Close()
	rows, errs := db.Query("select ip , name from clients")
	if errs != nil {
		log.Fatal(err)
	}
	defer rows.Close()
	for rows.Next() {
		err := rows.Scan(&ip, &name)
		a = a + fmt.Sprintf(s, ip, name)
		if err != nil {
			log.Fatal(err)
		}
		log.Println(ip, name)
	}
	err = rows.Err()
	if err != nil {
		log.Fatal(err)
	}
	fmt.Fprintf(w, a)
}
func sendHandler(w http.ResponseWriter, r *http.Request) {
	djson := r.FormValue("data")
	c := sysCmd{}
	json.Unmarshal([]byte(djson), &c)
	reply := ""
	for i:=0 ; i < len(c.Ip); i++ {
		ip := c.Ip[i]
		noErr, tempReply := sendToClient(ip,c.Cmd)
		if !noErr {
			tempReply = "Error connecting Please check Logs"
		}
		reply = reply + "<div class=\"resultIp\">" + ip + "</div> <div class=\"replyBody\">"+tempReply+"</div> <br>"
	}
	if reply == "" {
		reply = "<b>select pc<b>"
	}
	fmt.Fprintf(w, reply)
}
func main() {
	fmt.Print(dBData())
	fs := http.FileServer(http.Dir("."))
	http.Handle("/", fs)
	http.HandleFunc("/getSys/", getHandler)
	http.HandleFunc("/sendCmd/", sendHandler)
	http.ListenAndServe(":80", nil)
}
