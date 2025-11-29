<script lang="ts">
  import { goto } from '$app/navigation';
  import type { CodebaseModel } from '$lib/arch/api/Api';
  import CodebaseForm from '$lib/domain/codebases/CodebaseForm.svelte';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();
  let codebase = $state(data.codebase);

  async function handleAfterSave(id?: string) {
    // TODO: fix SVQK
    // await goto(`/codebases/${codebase.id}`);
    await goto(`/codebases/${id}`);
  }
</script>

<CodebaseForm bind:codebase {handleAfterSave} />
