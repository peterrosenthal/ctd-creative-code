<script>
  import { onInterval } from "../utils.js";

  const interests = [
    "design",
    "development",
    "games",
    "graphics",
    "generative art",
    "cliche portfolio subheaders"
  ];

  let interest = "design";
  let charactersToDisplay = interest.length;

  onInterval(() => cycleInterests(), 6000);

  function cycleInterests() {
    const subtractInterval = setInterval(() => subtractCharacters(subtractInterval), 1000 / interest.length);
  }

  function subtractCharacters(interval) {
    charactersToDisplay--;
    if (charactersToDisplay == 0) {
      clearInterval(interval);
      interest = interests[(interests.indexOf(interest) + 1) % interests.length];
      const addInterval = setInterval(() => addCharacters(addInterval), 1000 / interest.length);
    }
  }

  function addCharacters(interval) {
    charactersToDisplay ++;
    if (charactersToDisplay == interest.length) {
      clearInterval(interval);
    }
  }
</script>

<style>
  h1 {
    font-size: 4.5em;
    font-weight: 200;
  }

  .subtitle {
    font-size: 2em;
    font-weight: 300;
  }
</style>

<h1>Peter Rosenthal</h1>
<p class="subtitle">is interested in {interest.slice(0, charactersToDisplay)}</p>
