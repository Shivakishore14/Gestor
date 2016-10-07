package main

import b64 "encoding/base64"
import (
	"database/sql"
	"encoding/json"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"log"
	"net"
	"net/http"
	"strings"
)

var database = "gestor"
var user = "test"
var password = "test"
var clientPort = "9007"

type sysCmd struct {
	Ip  []string `json:"ip"`
	Cmd string   `json:"cmd"`
}
type clientsReply struct {
	Ip []string `json:ip`
	Reply []string `json:reply`
}
type getSys struct {
	Ip string `json:"ip"`
	Name string `json:"name"`  
}
type getSysArr struct {
	Array []getSys `json:"array"`
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
func deleteIp(s string) {
	database := "gestor"
	user := "test"
	password := "test"
	db, err := sql.Open("mysql", user + ":" + password + "@/" + database)
	if err = db.Ping(); err != nil {
		log.Fatal(err);
	}
	_, _ = db.Exec("Delete from clients where ip = ? ",s)

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
	reply := make([]byte, 1024)
	reply1 := ""
	tcpAddr, err := net.ResolveTCPAddr("tcp", servAddr)
	if err != nil {
		println("ResolveTCPAddr failed: ", err.Error())
		return false, ""
	}

	conn, err := net.DialTCP("tcp", nil, tcpAddr)
	if conn != nil {
		defer conn.Close()
	} else {
		println("Error")
		deleteIp(ip)
		return true, b64.StdEncoding.EncodeToString([]byte("Connection error"))
	}
	if err != nil {
		deleteIp(ip)
		println("Dial failed: ", err.Error())
		return false, ""
	}
	
	bCmd := []byte(cmd)
	_, err = conn.Write(bCmd)
	if err != nil {
		deleteIp(ip)
		println("Write to server failed: ", err.Error())
		return false, ""
	}
	if (string(bCmd[0]) == "Y") {
		_, err = conn.Read(reply)
		treply := string(reply)
		reply = []byte(treply)
		reply1 = b64.StdEncoding.EncodeToString(reply)
	}else {
		reply = []byte("operation done")
		reply1 = b64.StdEncoding.EncodeToString(reply)
	}
	if err != nil {
		deleteIp(ip)
		println("Write to server failed: ", err.Error())
		return false, reply1
	}
	finalreply :=  strings.TrimSpace(string(reply1))
	println("reply from server= ", finalreply)
	return true, finalreply
	
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
func getSysMobHandler(w http.ResponseWriter, r *http.Request) {
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
	
	syslist := make([]getSys,0 ,100)
	for rows.Next() {
		err := rows.Scan(&ip, &name)
		if err != nil {
			log.Fatal(err)
		}
		log.Println(ip, name)
		g:= &getSys{Ip:ip, Name:name}
		syslist = append(syslist, *g)
	}
	err = rows.Err()
	if err != nil {
		log.Fatal(err)
	}
	sysArr := &getSysArr{Array:syslist}
	json, je:= json.Marshal(sysArr)
	if string(json) == "{}" || je != nil {
		fmt.Fprintf(w, "NONE")
	}else{
		fmt.Fprintf(w, string(json))
	}
}
func isLoginValid(username string, pass string) bool {
	database := "gestor"
	user := "test"
	password := "test"
	db, err := sql.Open("mysql", user + ":" + password + "@/" + database)
	if err = db.Ping(); err != nil {
		log.Fatal(err);
		return false;
	}
	var passwd string
	defer db.Close()
	row := db.QueryRow("SELECT password FROM login WHERE username=?", username)
	e := row.Scan(&passwd)
	if e != nil {
		log.Println(e)
		return false;
	}
	//fmt.Println("-->>"+passwd+"-->"+dob)
	if pass ==  passwd {
		return true;
	}
	return false
}
func loginHandler(w http.ResponseWriter, r *http.Request) {
	user := r.FormValue("username")
	pass := r.FormValue("password")
	fmt.Printf(pass +"    " + user)
	//getDetails("14-VEC-244")
	isValid := isLoginValid(user, pass)
	if isValid {
		fmt.Fprintf(w, "Logged in")	
	}else {
		fmt.Fprintf(w, "Not loggedin")
	}
}

func sendHandler(w http.ResponseWriter, r *http.Request) {
	djson := r.FormValue("data")
	fmt.Println(djson)
	database := "gestor"
	user := "test"
	password := "test"
	db, err := sql.Open("mysql", user + ":" + password + "@/" + database)
	if err = db.Ping(); err != nil {
		log.Print(err);
	}
	defer db.Close()
	c := sysCmd{}
	json.Unmarshal([]byte(djson), &c)
	reply := make([]string, 0 ,10)
	temp := c.Ip	
	for i:=0 ; i < len(c.Ip); i++ {
		ip := c.Ip[i]
		noErr, tempReply := sendToClient(ip,c.Cmd)
		if !noErr {
			tempReply = "Error connecting (Please check Logs)"
		}
		reply = append(reply, tempReply)
		var t string
		row := db.QueryRow("SELECT name FROM clients WHERE ip=?", c.Ip[i])
		e := row.Scan(&t)
		if e != nil {
			log.Print(e)
		}else {
			temp[i] = t
		}
	}
	
	obj := &clientsReply{Ip: temp, Reply:reply}
	json, _:= json.Marshal(obj)
	if string(json) == "{}" {
		fmt.Fprintf(w, "NONE")
	}else{
		fmt.Fprintf(w, string(json))
	}
}
func main() {
	fmt.Print(dBData())
	fs := http.FileServer(http.Dir("."))
	http.Handle("/", fs)
	http.HandleFunc("/getSys/", getHandler)
	http.HandleFunc("/login/", loginHandler)
	http.HandleFunc("/sendCmd/", sendHandler)
	http.HandleFunc("/getSysMob/", getSysMobHandler)
	http.ListenAndServe(":80", nil)
}
