<script lang="ts">
  import type { DiagramModel } from '$lib/arch/api/Api';
  import { ClipboardCopy, Copy } from '@lucide/svelte';

  interface Props {
    diagram: DiagramModel;
  }

  let { diagram }: Props = $props();
  let canvas: HTMLDivElement;
  let scaleValue = $state(1);

  $effect(() => {
    if (canvas && diagram) {
      canvas.innerHTML = diagram.image;
    }
  });

  function copyToClipboard() {
    navigator.clipboard.writeText(diagram.text);
  }
</script>

<span class="copy-icon">
  <Copy onclick={copyToClipboard} />
</span>
<div id="canvas" style="--val:{scaleValue}" bind:this={canvas}></div>

<style lang="scss">
  #canvas {
    text-align: center;
    transform-origin: top left;
    transform: scale(var(--val));
  }

  .copy-icon:hover {
    cursor: pointer;
  }
</style>
