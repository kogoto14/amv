<script lang="ts">
  import { Square, SquareCheckBig } from '@lucide/svelte';

  export let done = false;
</script>

{#if done}
  <SquareCheckBig />
{:else}
  <Square />
{/if}

