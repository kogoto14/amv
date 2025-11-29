<script lang="ts">
  import { goto } from '$app/navigation';
  import type { CodebaseModel } from '$lib/arch/api/Api';
  import FormValidator from '$lib/arch/form/FormValidator';
  import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
  import ListTable, { ColumnsBuilder } from '$lib/arch/search/ListTable.svelte';
  import type { PageProps } from '../codebases/$types';
  import * as m from '$lib/paraglide/messages';

  let { data }: PageProps = $props();
  let { criteria } = $state(data);
  let { result } = $derived(data);

  const form = FormValidator.createForm({}, search);

  const columns = new ColumnsBuilder<CodebaseModel>()
    .add('#', 'c.id', () => codebaseIdAnchor)
    .add('name (placeholder)', 'c.name', (codebase) => codebase.name) // TODO: implement m.label_codebase_name() and use it at (placeholder)
    .add('url (placeholder)', 'c.url', (codebase) => codebase.url) // TODO: implement m.label_codebase_url() and use it at (placeholder)
    .add('token (placeholder)', 'c.token', (codebase) => codebase.token) // TODO: implement m.label_codebase_token() and use it at (placeholder)
    .build();

  function search() {
    goto(CriteriaUtils.encode(criteria));
  }
</script>

<section>
  <form use:form>
    <fieldset role="search">
      <input id="search" type="search" bind:value={criteria.text} />
      <input type="submit" value="Search" />
    </fieldset>
  </form>
</section>

<section>
  <a id="newCodebase" href="/codebases/new"> {m.newEntity()} </a>
</section>

<section>
  <ListTable
    {result}
    {columns}
    bind:pageControl={criteria.pageControl}
    bind:sortOrders={criteria.sortOrders}
    {search}
  />
</section>

{#snippet codebaseIdAnchor(codebase: CodebaseModel)}
  <a href={`/codebases/${codebase.id}`}>{`${codebase.id}`}</a>
{/snippet}
