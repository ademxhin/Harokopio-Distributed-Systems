In-Memory Key-Value Database example
===

---

Clone the repository:

```shell
git clone https://github.com/gkoulis/DS-Lab-KVDB.git
```

---

Run the example:

```shell
cd ds-lab-kv
./mvnw.cmd spring-boot:run
```

---

Open in browser: [localhost:8080/kv](http://localhost:8080/kv)

---

List all entries:

```shell
curl.exe -X GET "http://localhost:8080/kv"
```

---

Get entry:

```shell
curl.exe -X GET "http://localhost:8080/kv/foo"
```

---

Insert entry:

```shell
curl.exe -X POST "http://localhost:8080/kv/foo" -H "Content-Type: text/plain" -d "bar"
```

---

Update entry:

```shell
curl.exe -X PATCH "http://localhost:8080/kv/foo" -H "Content-Type: text/plain" -d "bar (v2)"
```

---

Create or update entry:

```shell
curl.exe -X PUT "http://localhost:8080/kv/foo2" -H "Content-Type: text/plain" -d "bar2"
```

---

Delete entry:

```shell
curl.exe -X DELETE "http://localhost:8080/kv/foo"
```
