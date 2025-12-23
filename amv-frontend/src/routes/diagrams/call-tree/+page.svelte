<script lang="ts">
  import { goto } from '$app/navigation';
  import type { CallTreeCriteriaModel, CallTreeElementModel } from '$lib/arch/api/Api';
  import CheckBox from '$lib/arch/form/CheckBox.svelte';
  import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
  import type { CriteriaModel } from './+page';
  import { ScrollText, Search } from '@lucide/svelte';
  import type { PageProps } from './$types';
  import ToCallTree from '$lib/domain/diagrams/ToCallTree.svelte';
  import InputField from '$lib/arch/form/InputField.svelte';
  import { m } from '$lib/paraglide/messages';

  let { data }: PageProps = $props();
  let { criteria: _criteria, methods, callTrees } = $derived(data);
  // svelte-ignore state_referenced_locally
  let criteria = $state(_criteria);
  let showExternalPackage = $state(true);
  let packageLevel = $state(3);

  $effect(() => {
    criteria = _criteria;
  });

  $effect(() => {
    search(criteria);
  });

  async function search(criteria: CriteriaModel) {
    await goto(CriteriaUtils.encode(criteria));
  }

  function url(qualifiedSignature: string | undefined) {
    return CriteriaUtils.encode({ methodCriteria: { text: qualifiedSignature } });
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
  <input id="search" type="search" bind:value={criteria.methodCriteria.text} autofocus />
</section>

{#if methods.list && methods.list!.length > 1}
  {@const andMoreCount = methods.pageResult!.count! - methods.list.length}
  <ul>
    {#each methods.list as method}
      <li>
        <a href={url(method.qualifiedSignature)}>{method.qualifiedSignature}</a>
      </li>
    {/each}

    {#if andMoreCount > 0}
      <li>
        ... and {andMoreCount} more
      </li>
    {/if}
  </ul>
{/if}

{#if callTrees.length > 0}
  <section>
    <article>
      {m.callHierarchyLegend()}
      <div>
        <span class="internal">{m.call()}</span>
        <span>: {m.internalCallDescription()}</span>
      </div>
      <div>
        <span class="data">{m.call()}</span>
        <span>: {m.dataClassDescription()}</span>
      </div>
      <div>
        <span class="setter">{m.call()}</span>
        <span class="notation-label">: {m.setterDescription()}</span>
      </div>
      <div>
        <span class="getter">{m.call()}</span>
        <span>: {m.getterDescription()}</span>
      </div>
    </article>
  </section>
{/if}

{#each callTrees as callTree}
  {@const method = callTree.method}

  <section class="container-fluid setting">
    <CheckBox
      id="call-tree"
      label={m.callHierarchy()}
      bind:checked={criteria.callTreeCriteria.callTreeRequired}
    />
    <CheckBox
      id="called-tree"
      label={m.calledHierarchy()}
      bind:checked={criteria.callTreeCriteria.calledTreeRequired}
    />
    <InputField
      id="internal-package-level"
      label={m.internalPackageLevel()}
      labelPos="after"
      type="number"
      min="1"
      width="4rem"
      bind:value={packageLevel}
    />
    <CheckBox
      id="show-external-package"
      label={m.showExternalPackageMethods()}
      bind:checked={showExternalPackage}
    />
  </section>

  <section class="container-fluid">
    <h3>{method.type}.{method.simpleSignature}</h3>

    <p>
      <strong>{m.qualifiedSignature()}:</strong>
      {method.qualifiedSignature}
    </p>

    {#if criteria.callTreeCriteria.callTreeRequired}
      <h4>{m.callHierarchy()}</h4>

      {#each callTree.callTree as e}
        {@render element(e)}
      {/each}
    {/if}

    {#if criteria.callTreeCriteria.calledTreeRequired}
      <h4>{m.calledHierarchy()}</h4>

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

        <ToCallTree signaturePattern={method.qualifiedSignature} />
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
