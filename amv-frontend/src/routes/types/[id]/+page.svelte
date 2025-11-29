<script lang="ts">
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();
  const type = data.type;
</script>

<!-- TODO: add a link to source -->

<article>
  <h2>Field Summary</h2>

  <table class="list striped">
    <thead>
      <tr>
        <th>Field Type</th>
        <th>Field Name</th>
      </tr>
    </thead>
    <tbody>
      {#each type.fields as field}
        <tr>
          <td>{field.type}</td>
          <td>{field.name}</td>
        </tr>
      {/each}
    </tbody>
  </table>
</article>

<article>
  <h2>Method Summary</h2>

  <table class="list striped">
    <thead>
      <tr>
        <th>Return Type</th>
        <th>Method Signature</th>
        <th>Additional Info</th>
      </tr>
    </thead>
    <tbody>
      {#each type.methods as method}
        <tr>
          <td>{method.returnType}</td>
          <!-- TODO: show method.parameters -->
          <!-- TODO: add a link to call tree -->
          <td>
            <a href={`#${method.simpleSignature}`} title={method.simpleSignature}>
              {method.simpleSignature}
            </a>
          </td>
          <td>
            {#if method.entryPoint}
              <strong>Entry Point:</strong> {method.entryPoint.httpMethod} {method.entryPoint.path}
            {/if}
            {#if method.crudPoints.length > 0}
              {#each method.crudPoints as crudPoint, i}
                <strong style={'visibility:' + (i === 0 ? 'visible' : 'hidden')}>CRUD:</strong>
                {crudPoint.crud}: {crudPoint.dataName || crudPoint.type}<br />
              {/each}
            {/if}
          </td>
        </tr>
      {/each}
    </tbody>
  </table>
</article>

<article>
  <h2>Method Detail</h2>
  {#each type.methods as method}
    <article> 
      <header id={method.simpleSignature}><strong>{method.simpleSignature}</strong></header>

      {#each method.calls as call}
        <div style="margin-left: 20px;">
          L.{call.lineNo}
          <strong>Call:</strong>
          {#if call.qualifiedSignature}
            {call.qualifiedSignature}
          {:else if call.interfaceSignature}
            {call.interfaceSignature} (interface)
          {:else}
            {call.fallbackSignature} (fallback) {call.unsolvedReason}
          {/if}
        </div>
      {/each}
    </article>
  {/each}
</article>
