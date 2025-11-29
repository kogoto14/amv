<script lang="ts">
  import { goto } from '$app/navigation';
  import type { CallTreeCriteriaModel, CallTreeElementModel } from '$lib/arch/api/Api';
  import CheckBox from '$lib/arch/form/CheckBox.svelte';
  import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
  import { ScrollText, Search } from '@lucide/svelte';
  import type { PageProps } from './$types';
  import ToCallTree from '$lib/domain/diagrams/ToCallTree.svelte';
  import InputField from '$lib/arch/form/InputField.svelte';

  let { data }: PageProps = $props();
  let { callTrees } = $derived(data);
  let { signaturePattern, callTreeRequired, calledTreeRequired } = $state(data.criteria);
  let showExternalPackage = $state(data.showExternalPackage);
  let packageLevel = $state(data.packageLevel);

  $effect(() => {
    search({
      signaturePattern,
      callTreeRequired,
      calledTreeRequired
    });
  });

  // const form = FormValidator.createForm({}, search);

  async function search(criteria: CallTreeCriteriaModel) {
    await goto(CriteriaUtils.encode(criteria));
  }

  function isInternalPackage(element: CallTreeElementModel, level: number): boolean {
    return element.elementTags.some((tag) => {
      if (!tag.startsWith('same-package-')) return false;
      const n = parseInt(tag.split('-')[2]);
      return n >= level;
    });
  }
</script>

<section class="container">
  <!-- svelte-ignore a11y_autofocus -->
  <input id="search" type="search" bind:value={signaturePattern} autofocus />
</section>

{#if callTrees.length > 0}
  <section>
    <article>
      The font of each element in the Call Tree has the following meanings:
      <div>
        <span class="internal">Call</span>
        <span>: Method within target internal package level</span>
      </div>
      <div>
        <span class="data">Call</span>
        <span>: Class that holds data such as a Dto, an Entity or their Builder ("Data Class")</span
        >
      </div>
      <div>
        <span class="setter">Call</span>
        <span class="notation-label">: Setter method of Data Class</span>
      </div>
      <div>
        <span class="getter">Call</span>
        <span>: Getter method of Data Class</span>
      </div>
    </article>
  </section>
{/if}

{#each callTrees as callTree}
  {@const method = callTree.method}

  <section class="container-fluid setting">
    <CheckBox id="call-tree" label="Call Tree" bind:checked={callTreeRequired} />
    <CheckBox id="called-tree" label="Called Tree" bind:checked={calledTreeRequired} />
    <InputField
      id="internal-package-level"
      label="Internal Package Level"
      labelPos="after"
      type="number"
      min="1"
      width="4rem"
      bind:value={packageLevel}
    />
    <CheckBox
      id="show-external-package"
      label="Show External Package Methods"
      bind:checked={showExternalPackage}
    />
  </section>

  <section class="container-fluid">
    <h3>{method.type}.{method.simpleSignature}</h3>

    <p>
      <strong>Qualified Signature:</strong>
      {method.qualifiedSignature}
    </p>

    {#if callTreeRequired}
      <h4>Call Tree</h4>

      {#each callTree.callTree as e}
        {@render element(e)}
      {/each}
    {/if}

    {#if calledTreeRequired}
      <h4>Called Tree</h4>

      {#each callTree.calledTree as e}
        {@render element(e)}
      {/each}
    {/if}
  </section>
{/each}

{#snippet element(element: CallTreeElementModel)}
  {@const method = element.method}
  {#if showExternalPackage || isInternalPackage(element, packageLevel)}
    <div style="margin-left: {element.depth}rem">
      {#if element.depth > 0}
        {element.call ? '-> ' : '<- '}
      {/if}

      <span
        class={`${
          isInternalPackage(element, packageLevel) ? 'internal ' : ''
        }${element.elementTags.join(' ')}`}
      >
        {#if method.dummy}
          <span>{method.name}</span>
        {:else}
          <span class={element.typeTags.join(' ')}>{method.type}</span>.<span
            class={element.methodTags.join(' ')}>{method.simpleSignature}</span
          >
        {/if}
      </span>

      {#if element.lineNoAt > 0}
        at
        {#if element.urlAt}
          <a href={element.urlAt}>L.{element.lineNoAt}</a>
        {:else}
          L.{element.lineNoAt}
        {/if}
      {/if}

      {#if !method.dummy}
        <a
          href={`/types/${method.namespace}.${method.type}#${method.simpleSignature}`}
          title={method.qualifiedSignature}
        >
          <Search />
        </a>
      {/if}

      {#if method.srcUrl}
        <a href={method.srcUrl} title={`Go to the declaration of ${method.simpleSignature}`}>
          <ScrollText />
        </a>

        <ToCallTree
          signaturePattern={method.qualifiedSignature}
          onclick={() => {
            signaturePattern = method.qualifiedSignature;
          }}
        />
      {/if}
    </div>
  {/if}
{/snippet}

<style lang="scss">
  .internal {
    font-weight: bold;
  }

  .data {
    color: var(--pico-color-green);
  }

  .setter {
    color: var(--pico-color-orange);
  }

  .getter {
    color: var(--pico-color-indigo);
  }
</style>
