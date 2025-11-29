<script lang="ts">
  import DiagramUtils from '$lib/domain/diagrams/DiagramUtils';
  import { ListTree } from '@lucide/svelte';

  interface Props {
    signaturePattern: string | undefined;
    onclick?: () => void;
  }

  let { signaturePattern, onclick }: Props = $props();
</script>

{#if signaturePattern}
  <a
    href={DiagramUtils.urlToCallTree({ signaturePattern })}
    data-tooltip={`Open Call Tree for ${signaturePattern}`}
    {onclick}
  >
    <ListTree />
  </a>
{/if}
