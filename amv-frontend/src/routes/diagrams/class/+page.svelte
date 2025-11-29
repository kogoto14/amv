<script lang="ts">
  import type { PageProps } from './$types';
  import { goto } from '$app/navigation';
  import CriteriaUtils from '$lib/arch/search/CriteriaUtils';

  let { data }: PageProps = $props();
  let { criteria } = $state(data);
  let { result } = $derived(data);
  let { classDiagram } = $derived(data);
  let canvas: HTMLDivElement;
  let scaleValue = $state(1);

  async function search() {
    await goto(CriteriaUtils.encode(criteria));
  }

  function url(qualifiedSignature: string) {
    return CriteriaUtils.encode({ text: qualifiedSignature });
  }

  $effect(() => {
    canvas.innerHTML = classDiagram;
  });
</script>

<section>
  <fieldset role="search">
    <!-- svelte-ignore a11y_autofocus -->
    <input id="search" type="search" bind:value={criteria.text} oninput={search} autofocus />
    <input type="submit" value="Search" />
  </fieldset>
</section>

<section>
  {#each result.list as type}
    <div>
      <a href={url(type.qualifiedName!)}>{type.qualifiedName}</a>
    </div>
  {/each}
</section>


<div id="canvas" style="--val:{scaleValue}" bind:this={canvas}></div>

<style lang="scss">
  #canvas {
    text-align: center;
    transform-origin: top left;
    transform: scale(var(--val));
  }
</style>
