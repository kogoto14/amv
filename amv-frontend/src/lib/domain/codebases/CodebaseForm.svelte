<script lang="ts">
  import type { CodebaseModel } from '$lib/arch/api/Api';
  import ApiHandler from '$lib/arch/api/ApiHandler';
  import FormValidator from '$lib/arch/form/FormValidator';
  import InputField from '$lib/arch/form/InputField.svelte';
  import TextArea from '$lib/arch/form/TextArea.svelte';
  import SelectBox from '$lib/arch/form/SelectBox.svelte';
  import CheckBox from '$lib/arch/form/CheckBox.svelte';
  import { messageStore } from '$lib/arch/global/MessageStore';
  import * as m from '$lib/paraglide/messages';
  import { string, date, boolean, number } from 'yup';
  import type { Snippet } from 'svelte';

  interface Props {
    codebase: CodebaseModel;
    updateMode?: boolean;
    handleAfterSave: (id?: string) => Promise<void>;
    handleAfterDelete?: (id?: string) => Promise<void>;
    renderExtra?: () => ReturnType<Snippet>;
    analyze?: () => Promise<void>;
  }

  let {
    codebase = $bindable(),
    updateMode = false,
    handleAfterSave,
    handleAfterDelete = async (id) => {},
    renderExtra,
    analyze = async () => {}
  }: Props = $props();

  const spec = {
    url: string().required()
  };

  const form = FormValidator.createForm(spec, save, del, analyze);
  

  async function save() {
    const response = await ApiHandler.handle<string>(fetch, (api) =>
      updateMode ? api.codebases.update(codebase.id, codebase) : api.codebases.save(codebase)
    );

    if (response) {
      await handleAfterSave(response);
      messageStore.show(m.saved());
    }
  }

  async function del() {
    const response = await ApiHandler.handle<string>(fetch, (api) =>
      api.codebases.delete(codebase.id, codebase)
    );

    if (response) {
      await handleAfterDelete();
      messageStore.show(m.deleted());
    }
  }
</script>

<form use:form>
  <div>
    <!-- TODO: implement m.label_codebase_url() and use it at (placeholder)-->
    <TextArea id="url" label="Url" bind:value={codebase.url} required={true} />
  </div>
  <div>
    <!-- TODO: implement m.label_codebase_name() and use it at (placeholder)-->
    <InputField id="name" label="Name" bind:value={codebase.name} />
  </div>
  <div>
    <!-- TODO: implement m.label_codebase_token() and use it at (placeholder)-->
    <InputField id="token" label="Token" bind:value={codebase.token} />
  </div>
  {#if renderExtra}
    {@render renderExtra()}
  {/if}
  <div class="grid">
    <div>
      <!--  TODO: fix SVQK also (function's name properties are not unique )-->
      <!-- <button type="submit" id="save" data-handler={save.name}> -->
      <button type="submit" id="save" data-handler={save}>
        {updateMode ? m.update() : m.register()}
      </button>
    </div>
    {#if updateMode}
      <div>
        <button type="submit" id="analyze" data-handler={analyze}> 解析 </button>
      </div>
      <div>
        <button type="submit" id="del" data-handler={del}>
          {m.delete()}
        </button>
      </div>
    {/if}
  </div>
</form>
