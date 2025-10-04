package com.tkfc.welus.definition.abstracts;

import com.tkfc.core.common.annotations.web.method.base.*;
import com.tkfc.core.common.annotations.web.method.json.*;
import com.tkfc.core.common.annotations.web.method.multipart.*;
import com.tkfc.core.common.annotations.web.method.view.*;
import com.tkfc.welus.definition.interfaces.MethodChecker;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;

import java.util.*;

/**
 * 方法检查器
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/8 9:59
 */
public abstract class BaseMethodChecker extends BaseLogPrinter implements MethodChecker {


    @Override
    public String[] getMethodMappings(MethodParameter methodParameter) {

        GetJson getJson = methodParameter.getMethodAnnotation(GetJson.class);
        if (Objects.nonNull(getJson)) {
            return getJson.path();
        }

        GetView getView = methodParameter.getMethodAnnotation(GetView.class);
        if (Objects.nonNull(getView)) {
            return getView.path();
        }

        PostJson postJson = methodParameter.getMethodAnnotation(PostJson.class);
        if (Objects.nonNull(postJson)) {
            return postJson.path();
        }

        PostView postView = methodParameter.getMethodAnnotation(PostView.class);
        if (Objects.nonNull(postView)) {
            return postView.path();
        }
        return new String[0];
    }

    @Override
    public Boolean isViewAccess(MethodParameter methodParameter) {
        //view
        DeleteView deleteView = methodParameter.getMethodAnnotation(DeleteView.class);
        GetView getView = methodParameter.getMethodAnnotation(GetView.class);
        HeadView headView = methodParameter.getMethodAnnotation(HeadView.class);
        OptionsView optionsView = methodParameter.getMethodAnnotation(OptionsView.class);
        PatchView patchView = methodParameter.getMethodAnnotation(PatchView.class);
        PostView postView = methodParameter.getMethodAnnotation(PostView.class);
        PutView putView = methodParameter.getMethodAnnotation(PutView.class);
        TraceView traceView = methodParameter.getMethodAnnotation(TraceView.class);
        return Objects.nonNull(deleteView) || Objects.nonNull(getView) ||
                Objects.nonNull(headView) || Objects.nonNull(optionsView) ||
                Objects.nonNull(patchView) || Objects.nonNull(postView) ||
                Objects.nonNull(putView) || Objects.nonNull(traceView);
    }

    @SuppressWarnings("all")
    @Override
    public Boolean isJsonAccess(MethodParameter methodParameter) {
        List<String> produces;
        produces = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(GetAndPost.class)).map(GetAndPost::produces).map(Arrays::asList).orElse(new ArrayList<>());
        produces = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(Delete.class)).map(Delete::produces).map(Arrays::asList).orElse(new ArrayList<>());
        produces = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(Get.class)).map(Get::produces).map(Arrays::asList).orElse(new ArrayList<>());
        produces = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(Head.class)).map(Head::produces).map(Arrays::asList).orElse(new ArrayList<>());
        produces = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(Options.class)).map(Options::produces).map(Arrays::asList).orElse(new ArrayList<>());
        produces = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(Patch.class)).map(Patch::produces).map(Arrays::asList).orElse(new ArrayList<>());
        produces = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(Post.class)).map(Post::produces).map(Arrays::asList).orElse(new ArrayList<>());
        produces = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(Put.class)).map(Put::produces).map(Arrays::asList).orElse(new ArrayList<>());
        produces = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(Trace.class)).map(Trace::produces).map(Arrays::asList).orElse(new ArrayList<>());

        //Json
        DeleteJson deleteJson = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(DeleteJson.class)).orElse(null);
        GetJson getJson = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(GetJson.class)).orElse(null);
        HeadJson headJson = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(HeadJson.class)).orElse(null);
        OptionsJson optionsJson = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(OptionsJson.class)).orElse(null);
        PatchJson patchJson = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(PatchJson.class)).orElse(null);
        PostJson postJson = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(PostJson.class)).orElse(null);
        GetAndPostJson getAndPostJson = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(GetAndPostJson.class)).orElse(null);
        PutJson putJson = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(PutJson.class)).orElse(null);
        TraceJson traceJson = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(TraceJson.class)).orElse(null);

        //Json
        DeleteMultipart deleteMultipart = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(DeleteMultipart.class)).orElse(null);
        GetMultipart getMultipart = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(GetMultipart.class)).orElse(null);
        HeadMultipart headMultipart = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(HeadMultipart.class)).orElse(null);
        OptionsMultipart optionsMultipart = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(OptionsMultipart.class)).orElse(null);
        PatchMultipart patchMultipart = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(PatchMultipart.class)).orElse(null);
        PostMultipart postMultipart = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(PostMultipart.class)).orElse(null);
        PutMultipart putMultipart = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(PutMultipart.class)).orElse(null);
        TraceMultipart traceMultipart = Optional.ofNullable(methodParameter).map(m -> m.getMethodAnnotation(TraceMultipart.class)).orElse(null);

        return produces.contains(MediaType.APPLICATION_JSON_VALUE) ||
                Objects.nonNull(deleteJson) || Objects.nonNull(getJson) || Objects.nonNull(getAndPostJson) ||
                Objects.nonNull(headJson) || Objects.nonNull(optionsJson) || Objects.nonNull(patchJson) ||
                Objects.nonNull(postJson) || Objects.nonNull(putJson) || Objects.nonNull(traceJson) ||
                Objects.nonNull(deleteMultipart) || Objects.nonNull(getMultipart) || Objects.nonNull(headMultipart) ||
                Objects.nonNull(optionsMultipart) || Objects.nonNull(patchMultipart) || Objects.nonNull(postMultipart) ||
                Objects.nonNull(putMultipart) || Objects.nonNull(traceMultipart);
    }
}
