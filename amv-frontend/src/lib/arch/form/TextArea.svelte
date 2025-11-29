<script lang="ts">
  import { ValidationMessage } from '@felte/reporter-svelte';

  interface Props {
    id: string;
    label: string;
    value: any;
    disabled?: boolean;
    // TODO: this required attribute should be resolved by validation schema
    required?: boolean;
  }

  let { id, label, value = $bindable(), disabled = false, required = false }: Props = $props();
</script>

<label for={id}>{label}{#if required}<span class="required">*</span>{/if}</label>
<ValidationMessage for={id} let:messages>
  <textarea {id} name={id} bind:value aria-describedby="invalid-{id}" {disabled}></textarea>
  <small id="invalid-{id}">{messages || ''}</small>
</ValidationMessage>
