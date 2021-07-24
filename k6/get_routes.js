import http from "k6/http"

export let options = {
  vus: 30,
  stages: [
    {duration: "1m", target: 5},
    {duration: "30s", target: 10}
  ]
}

export default function() {
  http.get("http://localhost:5050/organizations")
}