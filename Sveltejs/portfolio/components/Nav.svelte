<script>
  import { createEventDispatcher } from 'svelte';

  import NavItem from './NavItem.svelte';

  export let items = [];

  const dispatch = createEventDispatcher();

  function select(selection) {
    dispatch('select', { page: selection.item });
  }
</script>

<style>
</style>

{#each items as item}
<NavItem on:click={() => select({item})} text={item} />
{/each}
