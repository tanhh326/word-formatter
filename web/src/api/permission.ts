const Api = {
  MenuList: '/get-menu-list-i18n',
};

export function getMenuList(): {} {
  /*return request.get<MenuListResult>({
    url: Api.MenuList,
  });*/
  return {list: []}
}
