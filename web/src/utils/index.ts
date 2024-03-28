export function list2Options(list: any[], value: string, label: string) {
    return list.map(item => {
        return {
            label: item[label],
            value: item[value]
        }
    })
}

export function json2Options(json: {
    [key: string]: string
}) {
    return Object.keys(json).map(key => {
        return {
            label: json[key],
            value: key
        }
    })
}
