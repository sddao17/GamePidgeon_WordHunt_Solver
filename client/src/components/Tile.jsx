
import {motion} from "framer-motion";
import {TileBackground} from "../assets/images/TileBackground";
import {useState} from "react";

export const Tile = (letter, rotate) => {
	const [letterValue, setLetter] = useState(letter) || "";
	function handleChange(event) {
		setLetter(event.target.value.toUpperCase());
	}

	return (
			<motion.div
					className={"tile-container"}
					animate={{ rotate }}
					transition={{ type: "spring", duration: 1.5 }}>

				<div className={"tile"}>
					<TileBackground />
					<input type={"text"}
								 className={"tile-letter"}
								 onChange={handleChange}
								 value={letterValue} />
				</div>
			</motion.div>
	)
}
