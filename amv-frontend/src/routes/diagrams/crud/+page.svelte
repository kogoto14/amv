<script lang="ts">
  import type { PageProps } from './$types';
  import ToCallTree from '$lib/domain/diagrams/ToCallTree.svelte';

  let { data }: PageProps = $props();
  let { crud } = $derived(data);
</script>

<section class="table-wrap">
  <table class="list sticky">
    <colgroup>
      <col />
      {#each crud.tables as _}
        <col />
      {/each}
    </colgroup>
    <thead>
      <tr>
        <th class="corner"></th>
        {#each crud.tables as table}
          <th scope="col"><span title={table}>{table}</span></th>
        {/each}
      </tr>
    </thead>
    <tbody>
      {#each crud.entryPoints as entryPoint}
        {@const method = crud.methods[entryPoint]}
        <tr>
          <th scope="row" class="align-left row-head">
            <!-- <sub></sub><br /> -->
            <!-- <a href={method.srcUrl} title={entryPoint}>
              {method.type}.{method.simpleSignature}
            </a> -->
            <a href={`/types/${method.namespace}.${method.type}#${method.simpleSignature}`} title={entryPoint}>
              {method.type}.{method.simpleSignature}
            </a>

            <ToCallTree signaturePattern={method.qualifiedSignature} />
          </th>
          {#each crud.tables as table}
            <td>{crud.crud[entryPoint]?.[table]}</td>
          {/each}
        </tr>
      {/each}
    </tbody>
  </table>
</section>

<style lang="scss">
  .table-wrap {
    max-height: 80vh; /* vertical scroll */
    overflow: auto; /* creates the scroll container */
  }

  table.sticky {
    border-collapse: separate;
    border-spacing: 0;
    width: 100%;

    col:first-child {
      width: 30rem;
    }
    col:not(:first-child) {
      width: 4rem;
    }

    thead th {
      position: sticky;
      top: 0;
      background: #fff; /* must have a bg */
      z-index: 3; /* above body cells */
      box-shadow: 0 1px 0 #eaeaea; /* visual separation */
      height: 8rem;
      padding: 0 0 1rem 0;
      writing-mode: sideways-lr;
      text-align: left;
      white-space: normal;
    }
    th,
    td {
      background-clip: padding-box; /* avoids bleed under sticky */
    }

    .row-head {
      position: sticky;
      left: 0;
      background: #fff;
      z-index: 2; /* below the header row but above cells */
      box-shadow: 1px 0 0 #eaeaea;
      text-align: left;
      white-space: normal;
      word-break: break-all; 
    }

    .corner {
      position: sticky;
      top: 0;
      left: 0;
      z-index: 4; /* highest so it sits above both */
    }
  }
</style>
