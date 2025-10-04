const api = {}

api.get = id => {
    return {
        method: "post",
        url: "/${modelVarName}/detail",
        params: { id },
        headers: {
            "gateway-dev-proxy": import.meta.env.VITE_API_DEV_PROXY_ADDR
        }
    }
}

api.save = params => {
    return {
        method: "post",
        url: "/${modelVarName}/save",
        data: params,
        headers: {
            "gateway-dev-proxy": import.meta.env.VITE_API_DEV_PROXY_ADDR
        }
    }
}

api.del = params => {
    return {
        method: "post",
        url: "/${modelVarName}/delete",
        data: params,
        headers: {
            "gateway-dev-proxy": import.meta.env.VITE_API_DEV_PROXY_ADDR
        }
    }
}

api.page = params => {
    return {
        method: "post",
        url: "/${modelVarName}/page",
        data: params,
        headers: {
            "gateway-dev-proxy": import.meta.env.VITE_API_DEV_PROXY_ADDR
        }
    }
}

api.export = params => {
    return {
        method: "post",
        url: "/${modelVarName}/export",
        data: params,
        headers: {
            "gateway-dev-proxy": import.meta.env.VITE_API_DEV_PROXY_ADDR
        }
    }
}

<#if geneDataType == "tree">
api.asyncTreeData = params => {
    return {
        method: "post",
        url: "/${modelVarName}/asyncTreeData",
        data: params,
        headers: {
            "gateway-dev-proxy": import.meta.env.VITE_API_DEV_PROXY_ADDR
        }
    }
}

api.syncTreeData = params => {
    return {
        method: "post",
        url: "/${modelVarName}/syncTreeData",
        data: params,
        headers: {
            "gateway-dev-proxy": import.meta.env.VITE_API_DEV_PROXY_ADDR
        }
    }
}
</#if>
export default api