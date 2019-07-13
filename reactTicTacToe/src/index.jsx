import React from 'react'
import ReactDOM from 'react-dom'
import './index.css'

		class Field extends React.Component{
			constructor(props){
				super(props)
			}
			render(){
				return (
					<td>
						<div className={"field" + " " + this.props.figure} onClick={this.props.changeFigure} id={this.props.id}>
						</div>
					</td>
				)
			}
		}

		class PlayGround extends React.Component{
			constructor(props){
				super(props)
			}
            
			render(){
				
				const fields = this.props.playgroundMap.map((row, i) => 
					<tr key={'rownumber' + i}>
						{row.map((item, j) => <Field figure={item} changeFigure={this.props.onFieldClick} key={i + '/' + j} id={i + '/' + j}/>)}
					</tr>
				)
				
				return(
					<table className="playGround">
						<tbody>{fields}</tbody>
					</table>
				)
			}
		}

        function TurnLabel(props){
            return (<div>
                        <h2>{props.label}</h2>
                        <img src={require('/' + props.turn + '.jpg')} alt={props.turn}/>     
                    </div>)
        }

        function ActionLink(props){
            return <button onClick={props.onClickHandler} id={props.id}>{props.label}</button>
        }

        function ActionsList(props){
            const actions = props.actions.map((e,i) => <li key={'actionliitem/' + i}><ActionLink label={e.actionLabel} onClickHandler={props.actionLinkHandler} id={'action/' + e.id}/></li>)
            return <ul>{actions}</ul>
        }
    
        class Game extends React.Component{
            constructor(props){
                super(props)
                this.state = {
					playgroundMap : [
						['e', 'e', 'e'],
						['e', 'e', 'e'],
						['e', 'e', 'e']
					],
					turn: 'o',
                    gameOver: false,
                    label: 'Turn:',
                    actionsHistory: [],
                    displayAction: -1
				}
                this.state.actionsHistory = this.state.actionsHistory.concat([{playgroundMap: this.state.playgroundMap, turn: this.state.turn, label: this.state.label, gameOver: this.state.gameOver, actionLabel: 'Game Start', id: 0}])
            }
            
            onFieldClick = (e) => {
                if(this.state.gameOver) return
				if(e.target.getAttribute('class').split(' ')[1] !== 'e') return
                
                
				const id = e.target.getAttribute('id')
				const x = id.split('/')[0]
				const y = id.split('/')[1]
				const map = this.state.playgroundMap.map((arr) => arr.slice())
				map[x][y] = this.state.turn
                
                let newActionsHistory = null
                
                if(this.state.displayAction !== -1) newActionsHistory = this.state.actionsHistory.splice(0, parseInt(this.state.displayAction)+1)
                
                const actionNumber = newActionsHistory?newActionsHistory.length:this.state.actionsHistory.length
                const actionLabel = 'Action number: ' + actionNumber
                
                const action = {
                    playgroundMap: map,
                    turn: this.state.turn==='o'?'x':'o',
                    label: this.state.label,
                    gameOver: this.state.gameOver,
                    actionLabel: actionLabel,
                    id: actionNumber
                }
                
				this.setState(prevState => ({
                               playgroundMap: map, 
                               turn: this.state.turn==='o'?'x':'o', 
                               actionsHistory: newActionsHistory?newActionsHistory.concat([action]):prevState.actionsHistory.concat([action]),
                               displayAction: newActionsHistory?-1:prevState.displayAction
                }), () => {
                    if(this.checkWinner()){
                        this.endGame()
                    }
                })
			}
            
            endGame = () => {
                const actionsHistory = this.state.actionsHistory
                const lastAction = actionsHistory[actionsHistory.length-1]
                lastAction.actionLabel = 'Game Over !'
                lastAction.gameOver = true
                lastAction.label = 'Winner:'
                lastAction.turn = this.state.turn==='o'?'x':'o'
                this.setState(prevState => ({gameOver: true,
                                             label: 'Winner:', 
                                             turn: prevState.turn==='o'?'x':'o',
                                             actionsHistory: actionsHistory
                                            }))
            }
            
            checkWinner = () =>{
                const map = this.state.playgroundMap
                return(
                //poziomo
                ((map[0][0] === map[0][1])&&(map[0][1] === map[0][2])&&(map[0][0] !== 'e')) ||
                ((map[1][0] === map[1][1])&&(map[1][1] === map[1][2])&&(map[1][0] !== 'e')) ||
                ((map[2][0] === map[2][1])&&(map[2][1] === map[2][2])&&(map[2][0] !== 'e')) ||
                //pionowo
                ((map[0][0] === map[1][0])&&(map[1][0] === map[2][0])&&(map[0][0] !== 'e')) ||   
                ((map[0][1] === map[1][1])&&(map[1][1] === map[2][1])&&(map[0][1] !== 'e')) ||   
                ((map[0][2] === map[1][2])&&(map[1][2] === map[2][2])&&(map[0][2] !== 'e')) ||
                //na skos
                ((map[0][0] === map[1][1])&&(map[1][1] === map[2][2])&&(map[0][0] !== 'e')) ||
                ((map[2][0] === map[1][1])&&(map[1][1] === map[0][2])&&(map[2][0] !== 'e'))
                )
            }
            
            handleOnActionLinkClick = (e) => {
                const actionNumber = e.target.getAttribute('id').split('/')[1]
                
                if(this.state.actionsHistory.length-1 != actionNumber)
                    this.setState({displayAction: actionNumber})
                
                const action = this.state.actionsHistory[actionNumber]
                this.setState({
                    playgroundMap: action.playgroundMap,
                    turn: action.turn,
                    label: action.label,
                    gameOver: action.gameOver
                })
            }
            
            render(){
                return (
                    <div className={"game"}>
                        <div>
                            <PlayGround playgroundMap={this.state.playgroundMap} turn={this.state.turn} onFieldClick={this.onFieldClick}/>
                            <TurnLabel turn={this.state.turn} label={this.state.label}/>
                        </div>
                        <ActionsList actions={this.state.actionsHistory} actionLinkHandler={this.handleOnActionLinkClick}/>
                    </div>
                )
            }
        }


		ReactDOM.render(<Game/>, document.getElementById('root'))