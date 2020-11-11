<script>
  import Header from "./components/Header.svelte";
  import Nav from "./components/Nav.svelte";

  import { fade } from 'svelte/transition';

  const pages = [
    "Sketches",
    "Projects",
    "About"
  ];

  const colors = [
    "#c5e9b8",
    "#dce78d",
    "#f0ba8f"
  ];

  let currentPage = pages[1]; // Projects page

  function navSelected(event) {
    currentPage = event.detail.page;
    
  }
</script>

<style>
	main {
		text-align: center;
		padding-top: 1em;
    width: 100%;
    height: 100%;
    margin: 0;
	}

  .background {
    background-color:
  }
</style>

<main transition:fade style="background-color:{colors[pages.indexOf(currentPage)]}">
  <Header />
  <Nav items={pages} on:select={navSelected} />
</main>
