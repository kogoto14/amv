<script lang="ts">
  import type { PageProps } from './$types';
  import Diagram from '$lib/domain/diagrams/Diagram.svelte';
  import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
  import { goto } from '$app/navigation';
  import CheckboxGroup from '$lib/arch/form/CheckboxGroup.svelte';
  import type { CriteriaModel } from './+page';
  import InputField from '$lib/arch/form/InputField.svelte';
  import * as m from '$lib/paraglide/messages';

  let { data }: PageProps = $props();
  let { criteria: _criteria, methods, doc } = $derived(data);
  // svelte-ignore state_referenced_locally
  let criteria = $state(_criteria);

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
</script>

<section class="container">
  <!-- svelte-ignore a11y_autofocus -->
  <input id="search" type="search" bind:value={criteria.methodCriteria.text} autofocus />

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
          {m.and}
          {andMoreCount}
          {m.more}
        </li>
      {/if}
    </ul>
  {/if}
</section>

{#if doc.sequenceDiagram}
  <section class="container-fluid setting">
    <CheckboxGroup
      id="stereotype"
      label="Stereotype"
      options={doc.sequenceDiagram.participantStereotypes}
      bind:checkedValues={criteria.documentCriteria.participableStereotypes}
    />
    <InputField
      id="depth-options"
      label="Depth"
      type="number"
      bind:value={criteria.documentCriteria.depth}
    />
  </section>

  <section class="container-fluid">
    <Diagram diagram={doc.sequenceDiagram.diagram} />
  </section>
{/if}

{#if doc.classDiagram}
  <section class="container">
    <Diagram diagram={doc.classDiagram} />
  </section>
{/if}
