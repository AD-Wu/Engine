let api = [];
const apiDocListSize = 1
api.push({
    name: 'default',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'ESController',
    order: '1',
    link: 'es接口',
    desc: 'ES接口',
    list: []
})
api[0].list.push({
    alias: 'ProjectController',
    order: '2',
    link: '项目管理',
    desc: '项目管理',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: '/project/get',
    desc: '查询',
});
api[0].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: '/project/add',
    desc: '新增',
});
api[0].list[1].list.push({
    order: '3',
    deprecated: 'false',
    url: '/project/edit',
    desc: '修改',
});
api[0].list[1].list.push({
    order: '4',
    deprecated: 'false',
    url: '/project/delete',
    desc: '删除',
});
api[0].list.push({
    alias: 'SqlSourceController',
    order: '3',
    link: 'sql数据源管理',
    desc: 'sql数据源管理',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: '/sqlDatasource/',
    desc: '查询分页数据',
});
api[0].list[2].list.push({
    order: '2',
    deprecated: 'false',
    url: '/sqlDatasource/{id}',
    desc: '查询单条数据',
});
api[0].list[2].list.push({
    order: '3',
    deprecated: 'false',
    url: '/sqlDatasource/',
    desc: '新增数据',
});
api[0].list[2].list.push({
    order: '4',
    deprecated: 'false',
    url: '/sqlDatasource/',
    desc: '修改数据',
});
api[0].list[2].list.push({
    order: '5',
    deprecated: 'false',
    url: '/sqlDatasource/',
    desc: '删除数据',
});
api[0].list.push({
    alias: 'TestApi',
    order: '4',
    link: '',
    desc: '',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: '/test/now',
    desc: '',
});
api[0].list.push({
    alias: 'UserController',
    order: '5',
    link: '用户管理',
    desc: '用户管理',
    list: []
})
api[0].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: '/user/get',
    desc: '查询',
});
api[0].list[4].list.push({
    order: '2',
    deprecated: 'false',
    url: '/user/add',
    desc: '新增',
});
api[0].list[4].list.push({
    order: '3',
    deprecated: 'false',
    url: '/user/edit',
    desc: '修改',
});
api[0].list[4].list.push({
    order: '4',
    deprecated: 'false',
    url: '/user/delete',
    desc: '删除',
});
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    const theEvent = e;
    const code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code == 13) {
        const search = document.getElementById('search');
        const searchValue = search.value.toLocaleLowerCase();

        let searchGroup = [];
        for (let i = 0; i < api.length; i++) {

            let apiGroup = api[i];

            let searchArr = [];
            for (let i = 0; i < apiGroup.list.length; i++) {
                let apiData = apiGroup.list[i];
                const desc = apiData.desc;
                if (desc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                    searchArr.push({
                        order: apiData.order,
                        desc: apiData.desc,
                        link: apiData.link,
                        list: apiData.list
                    });
                } else {
                    let methodList = apiData.list || [];
                    let methodListTemp = [];
                    for (let j = 0; j < methodList.length; j++) {
                        const methodData = methodList[j];
                        const methodDesc = methodData.desc;
                        if (methodDesc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                            methodListTemp.push(methodData);
                            break;
                        }
                    }
                    if (methodListTemp.length > 0) {
                        const data = {
                            order: apiData.order,
                            desc: apiData.desc,
                            link: apiData.link,
                            list: methodListTemp
                        };
                        searchArr.push(data);
                    }
                }
            }
            if (apiGroup.name.toLocaleLowerCase().indexOf(searchValue) > -1) {
                searchGroup.push({
                    name: apiGroup.name,
                    order: apiGroup.order,
                    list: searchArr
                });
                continue;
            }
            if (searchArr.length === 0) {
                continue;
            }
            searchGroup.push({
                name: apiGroup.name,
                order: apiGroup.order,
                list: searchArr
            });
        }
        let html;
        if (searchValue == '') {
            const liClass = "";
            const display = "display: none";
            html = buildAccordion(api,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        } else {
            const liClass = "open";
            const display = "display: block";
            html = buildAccordion(searchGroup,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        }
        const Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;
            const links = this.el.find('.dd');
            links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
        };
        Accordion.prototype.dropdown = function (e) {
            const $el = e.data.el;
            $this = $(this), $next = $this.next();
            $next.slideToggle();
            $this.parent().toggleClass('open');
            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
            }
        };
        new Accordion($('#accordion'), false);
    }
}

function buildAccordion(apiGroups, liClass, display) {
    let html = "";
    let doc;
    if (apiGroups.length > 0) {
         if (apiDocListSize == 1) {
            let apiData = apiGroups[0].list;
            for (let j = 0; j < apiData.length; j++) {
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_' + apiData[j].link + '">' + apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                html += '<ul class="sectlevel2" style="'+display+'">';
                doc = apiData[j].list;
                for (let m = 0; m < doc.length; m++) {
                    let spanString;
                    if (doc[m].deprecated == 'true') {
                        spanString='<span class="line-through">';
                    } else {
                        spanString='<span>';
                    }
                    html += '<li><a href="#_1_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                }
                html += '</ul>';
                html += '</li>';
            }
        } else {
            for (let i = 0; i < apiGroups.length; i++) {
                let apiGroup = apiGroups[i];
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_' + apiGroup.name + '">' + apiGroup.order + '.&nbsp;' + apiGroup.name + '</a>';
                html += '<ul class="sectlevel1">';

                let apiData = apiGroup.list;
                for (let j = 0; j < apiData.length; j++) {
                    html += '<li class="'+liClass+'">';
                    html += '<a class="dd" href="#_'+apiGroup.order+'_'+ apiData[j].order + '_'+ apiData[j].link + '">' +apiGroup.order+'.'+ apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                    html += '<ul class="sectlevel2" style="'+display+'">';
                    doc = apiData[j].list;
                    for (let m = 0; m < doc.length; m++) {
                       let spanString;
                       if (doc[m].deprecated == 'true') {
                           spanString='<span class="line-through">';
                       } else {
                           spanString='<span>';
                       }
                       html += '<li><a href="#_'+apiGroup.order+'_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">'+apiGroup.order+'.' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                   }
                    html += '</ul>';
                    html += '</li>';
                }

                html += '</ul>';
                html += '</li>';
            }
        }
    }
    return html;
}