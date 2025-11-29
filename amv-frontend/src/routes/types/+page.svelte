<script lang="ts">
  import { goto } from '$app/navigation';
  import type { FieldModel, MethodModel, TypeModel } from '$lib/arch/api/Api';
  import FormValidator from '$lib/arch/form/FormValidator';
  import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
  import ListTable, { ColumnsBuilder } from '$lib/arch/search/ListTable.svelte';
  import type { PageProps } from '../types/$types';
  import * as m from '$lib/paraglide/messages';

  let { data }: PageProps = $props();
  let { criteria } = $state(data);
  let { result } = $derived(data);

  const form = FormValidator.createForm({}, search);

  const columns = new ColumnsBuilder<TypeModel>()
    .add('Namespace', 't.sourceFile.namespace', (type) => type.sourceFile.namespace, ['align-left'])
    .add('Name', 't.name', () => name, ['align-left'])
    .add('Unsolved Count', 't.unsolvedCnt', (type) => type.unsolvedCnt ?? 0)
    .add('Unsolved Rate', 't.unsolvedRate', (type) =>
      type.unsolvedRate ? `${(type.unsolvedRate * 100).toFixed(1)} %` : '0.0 %'
    )
    .build();

  async function search() {
    await goto(CriteriaUtils.encode(criteria));
  }
</script>

<section>
  <form use:form>
    <fieldset role="search">
      <!-- svelte-ignore a11y_autofocus -->
      <input id="search" type="search" bind:value={criteria.text} oninput={search} autofocus />
      <input type="submit" value="Search" />
    </fieldset>
  </form>
</section>

<!-- <section>
  <a id="newType" href="/types/new"> {m.newEntity()} </a>
</section> -->

<ListTable
  {result}
  {columns}
  bind:pageControl={criteria.pageControl}
  bind:sortOrders={criteria.sortOrders}
  {search}
/>

{#snippet name(type: TypeModel)}
  <a href={`/types/${type.id}`}>{`${type.name}`}</a>
{/snippet}
