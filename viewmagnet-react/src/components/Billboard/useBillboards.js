import {useCallback, useEffect, useState} from "react";

let dummyBbStat = [
    {
        id: "#BB1", position: [45.4, -75.7], status: "UP", interest: [0.2, 0.4, 0.2],
        ad: {id: "ADID#1", content: [0.2, 0.4, 0.1]}
    },
    {
        id: "#BB2", position: [45.2, -75.7], status: "UP", interest: [0.2, 0.4, 0.2],
        ad: {id: "ADID#6", content: [0.4, 0.1, 0.5]}
    }
];

export default function useBillboards() {

    let [ws, setWs] = useState();
    let [billboards, setBillboards] = useState(dummyBbStat);
    let addBillboard = useCallback((pos) => {
        setBillboards(b => [...b, {...b[0], position: pos}])
    }, []);
    useEffect(() => {

    }, []);

    return {billboards, addBillboard}
}