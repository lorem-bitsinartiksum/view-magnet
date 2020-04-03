import { useCallback, useEffect, useState } from "react";

let dummyBbStat = [
    {
        id: "#BB1", position: [45.4, -75.7], status: "UP", interest: [0.2, 0.4, 0.2],
        ad: { id: "ADID#1", content: [0.2, 0.4, 0.1] }
    },
    {
        id: "#BB2", adId: "23:43:23", position: [45.2, -75.7], status: "UP", interest: [0.2, 0.4, 0.2],
        ad: { id: "ADID#6", content: [0.4, 0.1, 0.5] }
    }
];

export default function useBillboards() {

    let [billboards, setBillboards] = useState(dummyBbStat);

    let post = (endpoint, data, then) => {
        fetch(`http://localhost:8000/${endpoint}`, {
            method: "POST",
            headers: {
                'Content-Type': "application/json"
            },
            body: JSON.stringify(data)
        })
            .then(resp => then && then(resp.json()))
            .catch(err => console.error(`Post to ${endpoint} went wrong, err: ${err}`))
    };

    let addBillboard = useCallback((data) => {
        post("billboard", { pos: data.pos, interest: data.interest })
    }, []);

    let changeInterest = useCallback((id, newInterest) => {
        post(`billboard/${id}`, newInterest)
    }, []);

    let shutdownBillboard = useCallback(id => fetch(`http://localhost:8000/${id}`, { method: "DELETE" }),
        []);


    useEffect(() => {
        let eventSrc = new EventSource("http://localhost:8000/status");

        eventSrc.onmessage = e => {
            let status = JSON.parse(e.data);
            let formatted = {
                id: status.billboardId, position: Object.values(status.position), status: status.status,
                interest: status.interest, ad: { id: status.adId, content: convertToColor(status.adId, ":") }
            };
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