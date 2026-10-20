import { createRequire } from 'module';
const require = createRequire('/home/zen/github-readme-terminal/GitAscii/');
const { chromium } = require('playwright-core');
import { readFileSync } from 'fs';

const browser = await chromium.launch({
  executablePath: '/usr/bin/google-chrome',
  args: ['--no-sandbox', '--disable-setuid-sandbox']
});
const page = await browser.newPage();
const svgText = readFileSync('/home/zen/Ana/design/logo-motion/states/1-broke-flies.svg', 'utf8');
await page.setContent(`<!DOCTYPE html><html><body>${svgText}</body></html>`);

const count = await page.evaluate(() => document.getAnimations().length);
console.log('Number of active animations in DOM:', count);
await browser.close();
