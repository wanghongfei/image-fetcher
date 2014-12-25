# Image Fetcher

### Usage
> `ifetch.sh http://www.baidu.com`
> `ifetch.sh http://www.qq.com`

### Build
* `maven clean install`

## Known issues
* Some format of images can not be recognized correctly. In that case `number.unknown` will be generated.
* Some images are written inside its css attribute therefore they won't be downloaded.
