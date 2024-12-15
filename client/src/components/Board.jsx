
import {Tile} from "./Tile";

export const Board = (dimension, letters, rotate) => {
	let key = 0;
	const grid = [];

	function getBoard() {
		// form the grid of tiles
		for (let i = 0; i < dimension; ++i) {
			grid.push([]);
			for (let j = i * dimension; j < (i + 1) * dimension; ++j) {
				grid[i].push(Tile(letters.charAt(j), rotate));
			}
		}

		return (
				<div
						className={"board"}
						style={{
							gridTemplate: "var(--tile-size) ".repeat(dimension) + "/" + " var(--tile-size) ".repeat(dimension),
						}}>
					{grid.map(
							row => {
								return (
										row.map(column => {
											return (
													<span key={key++}>
														{column}
													</span>
											)
										})
								);
							})
					}
				</div>
		)
	}

	return (
			<>{getBoard()}</>
	)
}
