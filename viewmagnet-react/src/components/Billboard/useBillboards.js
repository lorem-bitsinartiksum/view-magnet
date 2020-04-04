import { useCallback, useEffect, useState } from "react";

export default function useBillboards() {

    let [billboards, setBillboards] = useState([]);

    let req = (endpoint, data, then, method) => {
        fetch(`http://localhost:8000/${endpoint}`, {
            method: method,
            headers: {
                'Content-Type': "application/json"
            },
            body: JSON.stringify(data)
        })
            .then(resp => then && then(resp.json()))
            .catch(err => console.error(`Post to ${endpoint} went wrong, err: ${err}`))
    };

    let addBillboard = useCallback((data) => {
        req("billboard", { pos: data.pos, interest: data.interest }, null, "POST")
    }, []);

    let changeInterest = useCallback((id, newInterest) => {
        req(`billboard/${id}`, { interest: newInterest }, null, "PATCH")
    }, []);

    let shutdownBillboard = useCallback(id => fetch(`http://localhost:8000/${id}`, { method: "DELETE" }),
        []);

    useEffect(() => {
        //  let eventSrc = new WebSocket("ws://localhost:7000/api/admin/manager/status");
        let eventSrc = new EventSource("http://localhost:8000/billboard/status");

        eventSrc.onmessage = e => {
            let status = JSON.parse(e.data);
            let formatted = {
                id: status.billboardId, position: Object.values(status.billboardLocation), status: status.health,
                interest: status.interest, ad: { id: status.adId, content: [Math.random().toFixed(1), Math.random().toFixed(1), Math.random().toFixed(1)] }//convertToColor(status.adId, "-") }
            };
            console.log(formatted)
            setBillboards(bs => {
                let i = bs.findIndex(b => b.id === formatted.id);
                return i < 0 ? [...bs, formatted] : [...bs.slice(0, i), formatted, ...bs.slice(i + 1)]
            })
        };

        return () => eventSrc.close();
    }, []);

    return { billboards, addBillboard, shutdownBillboard, changeInterest }
}

function convertToColor(id, delimiter) {
    return id.split(delimiter).map(c => Number(c))
}