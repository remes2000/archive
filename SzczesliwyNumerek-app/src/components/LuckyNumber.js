import React from 'react'

const LuckyNumber = (props) => {
    const textDate = props.dayData.date.split('T')[0]
    const date = new Date(textDate)
    const polishDays = ['niedziela', 'poniedziałek', 'wtorek', 'środa', 'czwartek', 'piątek', 'sobota']

    return (
        <div style={{
            width: '15rem',
            height: '15rem',
            boxShadow: '0 3px 6px rgba(0,0,0,0.16), 0 3px 6px rgba(0,0,0,0.23)',
            textAlign: 'center'
        }}>
            <span style={{
                fontSize: '5rem'
            }}>
                {props.dayData.number}
            </span>
            <p>{polishDays[date.getUTCDay()]}</p>
            <p>{textDate}</p>
        </div>
    )
}

export default LuckyNumber