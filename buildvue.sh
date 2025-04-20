set -u;
set -e;
set -x;

cd simple-vue2-demo
npm run build

rm ../main/src/main/resources/static -rf
cp dist ../main/src/main/resources/static -r

