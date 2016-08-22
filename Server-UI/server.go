package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"log"
	"net"
	"net/http"
	"os"
)

var database = "gestor"
var user = "test"
var password = "test"

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
	return false
}
func sendToJava() {
	strEcho := "Hello\n"
	servAddr := "localhost:4444"
	tcpAddr, err := net.ResolveTCPAddr("tcp", servAddr)
	if err != nil {
		println("ResolveTCPAddr failed: ", err.Error())
		os.Exit(1)
	}

	conn, err := net.DialTCP("tcp", nil, tcpAddr)
	if err != nil {
		println("Dial failed: ", err.Error())
		os.Exit(1)
	}

	_, err = conn.Write([]byte(strEcho))
	if err != nil {
		println("Write to server failed: ", err.Error())
		os.Exit(1)
	}

	println("write to server = ", strEcho)

	reply := make([]byte, 1024)
	_, err = conn.Read(reply)

	if err != nil {
		println("Write to server failed: ", err.Error())
		os.Exit(1)
	}

	println("reply from server= ", string(reply))

	conn.Close()
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
	fmt.Printf(c.Cmd)
	fmt.Fprintf(w, "sk")
}
func main() {
	fmt.Print(dBData())
	fs := http.FileServer(http.Dir("."))
	http.Handle("/", fs)
	http.HandleFunc("/getSys/", getHandler)
	http.HandleFunc("/sendCmd/", sendHandler)
	http.ListenAndServe(":80", nil)
}
