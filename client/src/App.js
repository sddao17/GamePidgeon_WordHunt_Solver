
import './App.css';
import {useEffect, useState} from "react";
//import { motion } from "framer-motion";
import {Board} from "./components/Board";

function App() {
  const [input, setInput] = useState("");
  const [result, setResult] = useState();
  const [isLoading, setIsLoading] = useState(false);
  const [rotate, setRotate] = useState(0);

  let [min, max] = [4, 5];
  const dimension = Math.min(max, Math.max(min, Math.ceil(Math.sqrt(input.length))));

  useEffect(() => {
    wakeServer().then();

    // focus on the first tile on page load
    document.getElementsByClassName("tile-letter")[0].focus();
    }, []
  );

  async function wakeServer() {
    console.log("Waking up server...");

    // Send a test request to wake up the server in preparation for real user input
    const url = `https://server-dot-game-pigeon-word-hunt-solver.wl.r.appspot.com`;
    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json"}
    });

    console.log(`Server responded with status ${response.status}.`);
  }

  function handleInputChange(event) {
    setInput(event.target.value.toUpperCase());
  }

  async function handleKeyPress(event) {
    if (event.key === "Enter") {
      await handleSubmit();
    }
  }

  /**
   * Returns a JSON object representing the solved board.
   * @returns {Promise<any>} the promise state of the fetch request
   */
  async function handleSubmit() {
    const url = `https://server-dot-game-pigeon-word-hunt-solver.wl.r.appspot.com/board/solve?letters=${input}`;

    setIsLoading(true);
    setRotate(360);

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json"}
    });

    setResult(await response.json());
    setIsLoading(false);
    setRotate(0);

  }

  return (
    <div id={"body"}>
      <section>
        <h1 id={"title"}>
          <span data-text="Word Hunt">Word Hunt</span>
          <span data-text="Solver">Solver</span>
        </h1>
        <div id={"board-area"}>
          {Board(dimension, input.toUpperCase(), rotate)}
          <div id={"submission-area"}>
            <div id={"input-area"}
                 className={"submission-item"}>
              <label>
                Enter board letters:
              </label>
              <input className={"text-input"}
                     value={input}
                     maxLength={Math.pow(max, 2)}
                     onChange={handleInputChange}
                     onKeyDown={handleKeyPress}
                     autoCapitalize={"characters"} />
            </div>
            <button type={"submit"}
                    className={"submission-item"}
                    onClick={handleSubmit}>Submit</button>
          </div>
        </div>
      </section>
      <section>
        <p>{isLoading ?
            "Loading ..." :
            JSON.stringify(result, null, " ")}</p>
      </section>
    </div>
  );
}

export default App;
