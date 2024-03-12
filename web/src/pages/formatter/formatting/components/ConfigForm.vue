<script lang="ts" setup>
import {Card, Divider, Form, FormItem, InputNumber, Select} from "tdesign-vue-next";
import {
  ALIGNMENT,
  FONT_FAMILY,
  FONT_SIZE,
  INDENT_UNIT,
  LINE_SPACING_TYPE,
  LINE_SPACING_UNIT,
  LINE_SPACING_UNIT2,
  SPACING_UNIT
} from "../constant";
import {computed, ref} from "vue";

const props = defineProps<{
  type: string,

  config: {
    font: {
      nameFarEast: string,
      nameAscii: string,
      size: number
    },
    paragraphFormat: {
      alignment: number,
      indent: {
        left: {
          value: number,
          unit: string,
        },
        right: {
          value: number,
          unit: string,
        },
        firstLine: {
          value: number,
          unit: string,
        },
      },
      spacing: {
        before: {
          value: number | undefined,
          unit: string,
        },
        after: {
          value: number | undefined,
          unit: string,
        },
        line: {
          type: number,
          value: number,
          unit: string,
        },
      },
    }
  }
}>();

const lineSpacingUnitSelectOptions = computed(() => props.config.paragraphFormat.spacing.line.type == 2 ? LINE_SPACING_UNIT2 : LINE_SPACING_UNIT);

function onLineSpacingChange(value: number) {
  if (value == 2) {
    props.config.paragraphFormat.spacing.line.unit = LINE_SPACING_UNIT2[0].value;
  } else {
    props.config.paragraphFormat.spacing.line.unit = LINE_SPACING_UNIT[0].value;
  }
}

const spacingBeforeValueDisable = ref(false);

function onBeforeSpacingUnitChange(value: string) {
  if (value === 'AUTO') {
    props.config.paragraphFormat.spacing.before.value = undefined;
    spacingBeforeValueDisable.value = true;
  } else {
    props.config.paragraphFormat.spacing.before.value = 0;
    spacingBeforeValueDisable.value = false;
  }
}

const spacingAfterValueDisable = ref(false);

function onAfterSpacingUnitChange(value: string) {
  if (value === 'AUTO') {
    props.config.paragraphFormat.spacing.after.value = undefined;
    spacingAfterValueDisable.value = true;
  } else {
    props.config.paragraphFormat.spacing.after.value = 0;
    spacingAfterValueDisable.value = false;
  }
}

</script>

<template>
  <Card :title="type">
    <Form colon layout="inline">
      <FormItem label="中文">
        <Select v-model="config.font.nameFarEast" :options="FONT_FAMILY"/>
      </FormItem>
      <FormItem label="英文数字">
        <Select v-model="config.font.nameAscii" :options="FONT_FAMILY"/>
      </FormItem>
      <FormItem label="大小">
        <Select v-model="config.font.size" :options="FONT_SIZE" creatable filterable/>
      </FormItem>
      <FormItem label="对齐方式">
        <Select v-model="config.paragraphFormat.alignment" :options="ALIGNMENT"/>
      </FormItem>
      <Divider align="left">缩进</Divider>
      <FormItem label="左缩进">
        <InputNumber v-model="config.paragraphFormat.indent.left.value" theme="column"/>
        <Select v-model="config.paragraphFormat.indent.left.unit" :options="INDENT_UNIT" auto-width
                borderless/>
      </FormItem>
      <FormItem label="右缩进">
        <InputNumber v-model="config.paragraphFormat.indent.right.value" theme="column"/>
        <Select v-model="config.paragraphFormat.indent.right.unit" :options="INDENT_UNIT" auto-width
                borderless/>
      </FormItem>
      <FormItem label="首行缩进">
        <InputNumber v-model="config.paragraphFormat.indent.firstLine.value" theme="column"/>
        <Select v-model="config.paragraphFormat.indent.firstLine.unit" :options="INDENT_UNIT"
                auto-width
                borderless/>
      </FormItem>
      <Divider align="left">间距</Divider>
      <FormItem label="段前">
        <InputNumber v-model="config.paragraphFormat.spacing.before.value"
                     :disabled="spacingBeforeValueDisable"
                     theme="column"/>
        <Select v-model="config.paragraphFormat.spacing.before.unit" :options="SPACING_UNIT"
                auto-width
                borderless @change="onBeforeSpacingUnitChange"/>
      </FormItem>
      <FormItem label="段后">
        <InputNumber v-model="config.paragraphFormat.spacing.after.value"
                     :disabled="spacingAfterValueDisable"
                     theme="column"/>
        <Select v-model="config.paragraphFormat.spacing.after.unit" :options="SPACING_UNIT"
                auto-width borderless
                @change="onAfterSpacingUnitChange"/>
      </FormItem>
      <FormItem label="行距">
        <Select v-model="config.paragraphFormat.spacing.line.type" :options="LINE_SPACING_TYPE"
                auto-width
                borderless
                @change="onLineSpacingChange"/>
        <InputNumber v-model="config.paragraphFormat.spacing.line.value" theme="column"/>
        <Select v-model="config.paragraphFormat.spacing.line.unit"
                :options="lineSpacingUnitSelectOptions"
                auto-width
                borderless/>
      </FormItem>
    </Form>
  </Card>
</template>

<style scoped>

</style>
